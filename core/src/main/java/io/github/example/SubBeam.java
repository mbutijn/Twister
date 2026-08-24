package io.github.example;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;

public class SubBeam extends BeamUtils {
    private Vector3 root = new Vector3();
    private final Vector3 tip = new Vector3();
    private final Vector3 tmpTip = new Vector3();
    private final Car car;

    public SubBeam(float headingAngle, Color color) {
        this.yawAngle = headingAngle;
        this.car = new Car(color);
    }

    public void update(Vector3 root, float dt, float pitch, float yawAngleBeam) {
        this.root = root;
        this.yawAngle += 2 * dt;
        float yawAngleDifference = yawAngleBeam - yawAngle;
        float pitchLocal = (float) (pitch * Math.cos(yawAngleDifference));
        float length = 3;
        setAndRotateZY(tmpTip, length, 0, 0, pitchLocal);
        tip.set(root).add(tmpTip);

        car.update(tip, root, yawAngleDifference, pitch);
    }

    public void draw(ShapeRenderer shape) {
        shape.line(root.x, root.y, root.z, tip.x, tip.y, tip.z);
    }

    public void renderCar(ModelBatch modelBatch, ShapeRenderer shapeRenderer) {
        car.render(modelBatch);
        car.drawBoxEdges(shapeRenderer);
    }
}
