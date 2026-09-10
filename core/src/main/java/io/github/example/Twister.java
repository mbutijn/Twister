package io.github.example;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;

public class Twister implements Screen, InputProcessor {
    private static final Environment environment = new Environment();
    private static final Light light = new Light(environment);
    private static final Ground ground = new Ground();
    private final TwisterCamera twisterCamera;
    private final Controller controller;
    private final ShapeRenderer shape;
    private final ModelBatch modelBatch = new ModelBatch();
    private final Root root = new Root();
    private final Array<RotationArm> rotationArms;
    private float time = 0;
    private int touchDownY, touchDownX;
    private boolean paused;
    private int onRideArmPosition = 0;
    private static boolean isDay = true;

    public Twister() {
        twisterCamera = new TwisterCamera();
        shape = new ShapeRenderer();
        rotationArms = new Array<>();
        controller = new Controller();
        controller.updateTimeDependentValues();

        Array<String> colors = new Array<>();
        colors.add("red");
        colors.add("yellow");
        colors.add("green");
        colors.add("blue");

        int numberOfArms = 4;
        for (int i = 0; i < numberOfArms; i++) {
            rotationArms.add(new RotationArm(
                (float) (i * 2 * Math.PI / numberOfArms),
                i * controller.getMaxPhaseDifference() / numberOfArms,
                colors.get(i)));
        }

        // Set up the input processor
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(controller.getStage());
        multiplexer.addProcessor(this);
        Gdx.input.setInputProcessor(multiplexer);
        twisterCamera.update();
    }

    @Override
    public void render(float delta) {
        twisterCamera.handleInput();

        // Clear screen
        Gdx.gl.glClearColor(isDay ? 0.5f : 0.05f, isDay ?  0.75f : 0.05f, isDay ? 1.0f : 0.1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        float dt = Gdx.graphics.getDeltaTime();

        if (!paused) {
            controller.updateTimeDependentValues();
            if (controller.isPhaseDifferenceNeedsChange()) {
                setPhaseDifferences();
            }

            float speed = dt * controller.getRotationSpeedMain();
            time += dt * controller.getPitchFrequency();
            if (!isDay) {
                light.update(time);
            }

            root.update(speed);
            for (RotationArm rotationArm : rotationArms) {
                rotationArm.update(time, speed, controller.getRotationSpeedSub() * dt, controller.getPitchEquilibrium(), controller.getPitchAmplitude());
            }
//            paused = true;
        }

        modelBatch.begin(twisterCamera.getPerspectiveCamera());
        ground.render(modelBatch);

        for (RotationArm rotationArm : rotationArms) {
            rotationArm.getBeam().draw(modelBatch, environment);
            rotationArm.drawHydraulics(modelBatch, environment);
        }

        root.render(modelBatch, environment);

        // render the car systems
        for (RotationArm rotationArm : rotationArms) {
            rotationArm.renderSubRotationSystem(twisterCamera.getPerspectiveCamera(), environment, dt);
        }

        modelBatch.end();

//        shape.setProjectionMatrix(twisterCamera.combined);
//        shape.begin(ShapeRenderer.ShapeType.Line);
//        drawAxes(shape);
//        shape.setColor(Color.BLACK);
//        root.drawBoxEdges(shape);
//
//        for (RotationArm rotationArm : rotationArms) {
//            rotationArm.getBeam().drawBoxEdges(shape);
//        }
//        shape.end();

        controller.update(delta);
        controller.drawTrueValues();
        controller.render();

        SubRotationSystem srs = rotationArms.get(onRideArmPosition).getSubRotationSystem();
        twisterCamera.setOnRidePosition(srs.getOnRideCameraPosition(), srs.getOnRideCameraRotation());
    }

    static void switchDayNight() {
        isDay = !isDay;
        light.switchDayNight(isDay);
        ground.switchDayNight(isDay);
    }

    private void drawAxes(ShapeRenderer shape) {
        // X axis - red
        shape.setColor(Color.RED);
        shape.line(
            0, 0, 0,
            5, 0, 0
        );
        // Y axis - green
        shape.setColor(Color.GREEN);
        shape.line(
            0, 0, 0,
            0, 5, 0
        );
        // Z axis - blue
        shape.setColor(Color.BLUE);
        shape.line(
            0, 0, 0,
            0, 0, 5
        );
    }

    public void setPhaseDifferences() {
        //System.out.println("maxPhaseDifference: " + maxPhaseDifference);
        int numberOfArms = rotationArms.size;
        for (RotationArm rotationArm : rotationArms) {
            int i = rotationArms.indexOf(rotationArm, true);
            rotationArm.setPhaseDifference(i * controller.getMaxPhaseDifference() / numberOfArms);
        }
    }

    @Override
    public void show() {
    }

    @Override
    public void resize(int width, int height) {
        twisterCamera.resize(width, height);
        Gdx.gl.glViewport(0, 0, width, height);
        controller.getStage().getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        shape.dispose();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public boolean keyDown(int keycode) {
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) { // reset function
            twisterCamera.resetMiddlePoint();
        }

        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            paused = !paused;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.C)) {
            twisterCamera.toggleInRide();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.V)) {
            if (twisterCamera.isOnRide()) {
                onRideArmPosition++;
                if (onRideArmPosition >= rotationArms.size) {
                    onRideArmPosition = 0;
                }
            }
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        touchDownX = screenX;
        touchDownY = screenY;
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        twisterCamera.handleDragged(screenX - touchDownX, screenY - touchDownY);

        touchDownX = screenX;
        touchDownY = screenY;
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        twisterCamera.handleScrolled(amountY);
        return false;
    }
}
