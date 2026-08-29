package io.github.example;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.math.MathUtils;

public class Twister implements Screen, InputProcessor {
    private final PerspectiveCamera camera;
    private final Controller controller;
    private final ShapeRenderer shape;
    private final com.badlogic.gdx.math.Vector3 cameraPos = new com.badlogic.gdx.math.Vector3();
    private final ModelBatch modelBatch = new ModelBatch();
    private final Root root = new Root();
    private float cameraAngle = 45f;
    private float cameraPitch = 30f;
    private float cameraDistance = 20f;
    private float cameraFocusX = 0f;
    private float cameraFocusY = 1f;
    private float cameraFocusZ = 0f;
    private int touchDownY, touchDownX;
    private final Array<RotationArm> rotationArms;
    private float time = 0;
    private boolean paused;

    public Twister() {
        camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        camera.lookAt(cameraFocusX, cameraFocusY, cameraFocusZ);
        camera.near = 0.1f;
        camera.far = 1000f;
        camera.update();

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
        updateCamera();
    }

    private void updateCamera() {
        // Use MathUtils helpers and a reusable cameraPos to avoid allocations
        float horizontalDistance = cameraDistance * MathUtils.cosDeg(cameraPitch);
        float y = cameraDistance * MathUtils.sinDeg(cameraPitch);
        // start at +X on the horizontal plane then rotate around Y by the heading angle
        cameraPos.set(horizontalDistance, y, 0f);
        cameraPos.rotate(com.badlogic.gdx.math.Vector3.Y, cameraAngle);
        camera.position.set(cameraPos);
        camera.lookAt(cameraFocusX, cameraFocusY, cameraFocusZ);
        camera.up.set(0, 1, 0);
        camera.update();
    }

    @Override
    public void render(float delta) {
        handleInput();

        // Clear screen
        Gdx.gl.glClearColor(0.05f, 0.25f, 0.15f, 1);
        Gdx.gl.glClear(
            GL20.GL_COLOR_BUFFER_BIT |
                GL20.GL_DEPTH_BUFFER_BIT
        );

        float dt = Gdx.graphics.getDeltaTime();

        if (!paused) {
            controller.updateTimeDependentValues();
            if (controller.isPhaseDifferenceNeedsChange()) {
                setPhaseDifferences();
            }

            float speed = dt * controller.getRotationSpeedMain();
            time += speed / 0.75f;

            root.update(speed);
            for (RotationArm rotationArm : rotationArms) {
                rotationArm.update(time, speed, controller.getRotationSpeedSub() * dt, controller.getPitchEquilibrium(), controller.getPitchAmplitude());
            }
//            paused = true;
        }

        modelBatch.begin(camera);
        for (RotationArm rotationArm : rotationArms) {
            rotationArm.getBeam().draw(modelBatch);
            rotationArm.drawHydraulics(modelBatch);
        }

        root.render(modelBatch);

        // render the cars
        for (RotationArm rotationArm : rotationArms) {
            rotationArm.renderSubRotationSystem(camera, dt);
        }

        modelBatch.end();

//        shape.setProjectionMatrix(camera.combined);
//        shape.begin(ShapeRenderer.ShapeType.Line);
//        drawAxes(shape);
//        shape.setColor(Color.BLACK);
//        root.drawBoxEdges(shape);
//
//        for (RotationArm rotationArm : rotationArms) {
//            rotationArm.getBeam().drawBoxEdges(shape);
//        }
//
//        shape.end();

        controller.update(delta);
        controller.render();

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

    private void handleInput() {
        // Shift camera vertically
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            cameraFocusY += 0.1f;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            cameraFocusY -= 0.1f;
        }

        // Change field of view
        if (Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT)) {
            camera.fieldOfView += 0.1f;
            System.out.println("Camera FOV: " + camera.fieldOfView);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
            camera.fieldOfView -= 0.1f;
            System.out.println("Camera FOV: " + camera.fieldOfView);
        }

        // Prevent weird camera positions
        cameraPitch = MathUtils.clamp(cameraPitch, -30, 89);
        cameraDistance = Math.max(1f, cameraDistance);

        updateCamera();
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
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();
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
            cameraFocusX = 0;
            cameraFocusY = 1;
            cameraFocusZ = 0;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            paused = !paused;
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
        cameraAngle -= 0.25f * (screenX - touchDownX);
        cameraPitch += 0.25f * (screenY - touchDownY);

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
        cameraDistance += amountY;
        return false;
    }
}
