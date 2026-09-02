package io.github.example;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

public class TwisterCamera extends PerspectiveCamera {
    private float angle = 45f;
    private float pitch = 30f;
    private float distance = 20f;
    private float focusX = 0f;
    private float focusY = 1f;
    private float focusZ = 0f;

    public TwisterCamera() {
        super(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // initial camera setup
        lookAt(focusX, focusY, focusZ);
        near = 0.1f;
        far = 1000f;
        update();
    }

    public void update() {
        // Prevent weird camera positions
        pitch = MathUtils.clamp(pitch, -30, 89);
        distance = Math.max(1f, distance);

        // Use MathUtils helpers and a reusable position to avoid allocations
        float horizontalDistance = distance * MathUtils.cosDeg(pitch);
        float y = distance * MathUtils.sinDeg(pitch);
        // start at +X on the horizontal plane then rotate around Y by the heading angle
        position.set(horizontalDistance, y, 0f);
        position.rotate(Vector3.Y, angle);
        // apply to this camera (inherited fields)
        lookAt(focusX, focusY, focusZ);
        up.set(0, 1, 0);
        super.update();
    }

    public PerspectiveCamera getPerspectiveCamera() {
        return this;
    }

    public void resize(int width, int height) {
        viewportWidth = width;
        viewportHeight = height;
        update();
    }

    public void handleInput() {
        // Shift camera vertically
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            focusY += 0.1f;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            focusY -= 0.1f;
        }

        // Change field of view
        if (Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT)) {
            fieldOfView += 0.1f;
            System.out.println("Camera FOV: " + fieldOfView);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
            fieldOfView -= 0.1f;
            System.out.println("Camera FOV: " + fieldOfView);
        }

        update();
    }

    public void handleDragged(int xDiff, int yDiff) {
        angle -= 0.25f * xDiff;
        pitch += 0.25f * yDiff;
    }

    public void resetMiddlePoint() {
        focusX = 0;
        focusY = 1;
        focusZ = 0;
    }

    public void handleScrolled(float amountY) {
        distance += amountY;
    }
}
