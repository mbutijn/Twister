package io.github.example;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Quaternion;

public class TwisterCamera extends PerspectiveCamera {
    private float angle = 45f;
    private float pitch = 30f;
    private float distance = 20f;
    private float focusX = 0f;
    private float focusY = 1f;
    private float focusZ = 0f;
    private boolean onRide = false;
    private final Vector3 onRidePosition = new Vector3(0, 0, 0);
    private final Quaternion onRideRotation = new Quaternion();

    public TwisterCamera() {
        super(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // initial camera setup
        lookAt(focusX, focusY, focusZ);
        near = 0.1f;
        far = 1000f;
        update();
    }

    public void update() {
        if (onRide) {
            // place camera using the model rotation to compute direction and up directly (avoids lookAt numerical issues)
            position.set(onRidePosition);
            // forward = -Z in model space -> transform into world direction
            direction.set(0f, 0f, -1f);
            onRideRotation.transform(direction);
            // up = +Y in model space -> transform into world up
            up.set(0f, 1f, 0f);
            onRideRotation.transform(up);
        } else {
            // Prevent weird camera positions
            pitch = MathUtils.clamp(pitch, 0, 89);
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
        }

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
        if (!onRide) {
            if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
                focusY += 0.1f;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                focusY -= 0.1f;
            }
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
        if (!onRide) {
            angle -= 0.25f * xDiff;
            pitch += 0.25f * yDiff;
        }
    }

    public void resetMiddlePoint() {
        if (!onRide) {
            focusX = 0;
            focusY = 1;
            focusZ = 0;
        }
    }

    public void handleScrolled(float amountY) {
        if (!onRide) {
            distance += amountY;
        }
    }

    public void toggleInRide() {
        onRide = !onRide;
    }

    public void setOnRidePosition(Vector3 onRidePosition, Quaternion onRideRotation) {
        // copy values to avoid shared mutable objects
        this.onRidePosition.set(onRidePosition);
        this.onRideRotation.set(onRideRotation);
    }
}
