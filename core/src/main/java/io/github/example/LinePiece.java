package io.github.example;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class LinePiece {
    private Vector2 vector1;
    private final Vector2 vector2;
    private float baseDirection;
    private final float length;
    private final Vector2 tmp = new Vector2();
    private final int index;
    private double angle;
    private double phaseDifference;

    public LinePiece(Vector2 v1, float baseDirection, int index) {
        this.vector1 = v1;
        this.length = 20;
        this.baseDirection = baseDirection;
        this.index = index;
        this.angle = baseDirection;
        this.phaseDifference = 0;

        // Use Vector2 rotation to compute vector2 relative to vector1 using reusable tmp
        this.vector2 = new Vector2();
        tmp.set(length, 0);
        tmp.rotateRad(baseDirection);
        this.vector2.set(vector1).add(tmp);
    }

    public void updateWithV1(Vector2 v1, long time) {
        this.vector1 = v1;
        update(time);
    }

    public void updatePhaseDifference(long time) {
        this.phaseDifference = angle - Spider.getLineVelocity() * time;
    }

    public void update(long time) {
        angle = Spider.getLineVelocity() * time + phaseDifference;
        float direction = baseDirection + index * 0.1f * (float) Math.sin(angle);

        tmp.set(length, 0);
        tmp.rotateRad(direction);
        vector2.set(vector1).add(tmp);

        baseDirection += Spider.getBaseVelocity();
    }

    public Vector2 getVector2() {
        return vector2;
    }

    public void draw(ShapeRenderer shape) {
        shape.rectLine(vector1.x, vector1.y, vector2.x, vector2.y,  1f);
    }

    public int getIndex() {
        return index;
    }
}
