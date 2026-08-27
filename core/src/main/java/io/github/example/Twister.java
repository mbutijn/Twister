package io.github.example;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.math.MathUtils;

public class Twister implements Screen, InputProcessor {
    private final PerspectiveCamera camera;
    private final ShapeRenderer shape;
    private final com.badlogic.gdx.math.Vector3 cameraPos = new com.badlogic.gdx.math.Vector3();
    private final ModelBatch modelBatch = new ModelBatch();
    private final Root root = new Root();

    private float cameraAngle = 45f;
    private float cameraPitch = 30f;
    private float cameraDistance = 20f;
    private float midPointX = 0f;
    private float midPointY = 1f;
    private float midPointZ = 0f;
    private int touchDownY, touchDownX;

//    private final Array<Beam> beams;
    private final Array<RotationArm> rotationArms;
    private float time = 0;
    private float maxPhaseDifference = (float) Math.PI;
    private float targetMaxPhaseDifference = (float) Math.PI;
    private boolean paused;

    public Twister() {
        camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        camera.lookAt(0, 0, 0);
        camera.near = 0.1f;
        camera.far = 1000f;
        camera.update();

        shape = new ShapeRenderer();
//        beams = new Array<>();
        rotationArms = new Array<>();

        Array<String> colors = new Array<>();
        colors.add("red");
        colors.add("yellow");
        colors.add("green");
        colors.add("blue");

        int numberOfArms = 4;
        for (int i = 0; i < numberOfArms; i++) {
            rotationArms.add(new RotationArm(
                (float) (i * 2 * Math.PI / numberOfArms),
                i * maxPhaseDifference / numberOfArms,
                colors.get(i)));
        }

        // Set up the input processor
        Gdx.input.setInputProcessor(this);
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
        camera.lookAt(midPointX, midPointY, midPointZ);
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
            time += dt;

            float difference = targetMaxPhaseDifference - maxPhaseDifference;
            if (difference > 0) {
                maxPhaseDifference = difference > 0.01f ? maxPhaseDifference + 0.01f : targetMaxPhaseDifference;
                setPhaseDifferences();
            }
            if (difference < 0) {
                maxPhaseDifference = difference < -0.01f ? maxPhaseDifference - 0.01f : targetMaxPhaseDifference;
                setPhaseDifferences();
            }

            float rotationAngleIncrease = 0.75f * dt;
            root.update(rotationAngleIncrease);
            for (RotationArm rotationArm : rotationArms) {
                rotationArm.update(time, rotationAngleIncrease, dt);
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
        // Rotate horizontally
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            midPointX += (0.1f * MathUtils.cosDeg(cameraAngle));
            midPointZ += (0.1f * MathUtils.sinDeg(cameraAngle));
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            midPointX -= (0.1f * MathUtils.cosDeg(cameraAngle));
            midPointZ -= (0.1f * MathUtils.sinDeg(cameraAngle));
        }

        // Rotate vertically
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            midPointX += 0.1f * MathUtils.sinDeg(cameraPitch) * MathUtils.cosDeg(cameraAngle);
            midPointY += 0.1f * MathUtils.cosDeg(cameraPitch);
            midPointZ += 0.1f * MathUtils.sinDeg(cameraPitch) * MathUtils.sinDeg(cameraAngle);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            midPointX -= 0.1f * MathUtils.sinDeg(cameraPitch) * MathUtils.cosDeg(cameraAngle);
            midPointY -= 0.1f * MathUtils.cosDeg(cameraPitch);
            midPointZ -= 0.1f * MathUtils.sinDeg(cameraPitch) * MathUtils.sinDeg(cameraAngle);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT)) {
            camera.fieldOfView += 0.1f;
            System.out.println("Camera VOF: " + camera.fieldOfView);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
            camera.fieldOfView -= 0.1f;
            System.out.println("Camera VOF: " + camera.fieldOfView);
        }

        // Prevent weird camera positions
        cameraPitch = MathUtils.clamp(cameraPitch, 0, 89);
        cameraDistance = Math.max(1f, cameraDistance);

        updateCamera();
    }

    public void setPhaseDifferences() {
        //System.out.println("maxPhaseDifference: " + maxPhaseDifference);
        int numberOfArms = rotationArms.size;
        for (RotationArm rotationArm : rotationArms) {
            int i = rotationArms.indexOf(rotationArm, true);
            //rotationArm.getBeam().setPhaseDifference(i * maxPhaseDifference / numberOfArms);
            rotationArm.setPhaseDifference(i * maxPhaseDifference / numberOfArms);
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
            midPointX = 0;
            midPointY = 1;
            midPointZ = 0;
        }

        // Change the phase differences per beam
        if (Gdx.input.isKeyPressed(Input.Keys.PAGE_UP)) {
            targetMaxPhaseDifference += (float) (0.5f * Math.PI);
            targetMaxPhaseDifference = (float) MathUtils.clamp(targetMaxPhaseDifference, 0, 4 * Math.PI);
            //System.out.println("targetMaxPhaseDifference:" + targetMaxPhaseDifference);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.PAGE_DOWN)) {
            targetMaxPhaseDifference -= (float) (0.5f * Math.PI);
            targetMaxPhaseDifference = (float) MathUtils.clamp(targetMaxPhaseDifference, 0, 4 * Math.PI);
            //System.out.println("targetMaxPhaseDifference:" + targetMaxPhaseDifference);
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
