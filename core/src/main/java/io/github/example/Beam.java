package io.github.example;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class Beam extends BeamUtils {
    private final Vector3 begin = new Vector3();
    private final Vector3 corner = new Vector3();
    private final Vector3 end = new Vector3();
    private final Vector3 tmpCorner = new Vector3();
    private final Vector3 tmpEnd = new Vector3();
    private float phaseDifference;
    private final Array<SubBeam> subBeams;
    private final Color color;

    public Beam(float baseDirection, float phaseDifference, Color color) {
        this.yawAngle = baseDirection;
        this.color = color;
        this.phaseDifference = phaseDifference;

        this.subBeams = new Array<>();
        int numberSubBeams = 4;
        for (int i = 0; i < numberSubBeams; i++) {
            subBeams.add(new SubBeam((float) (i * 2 * Math.PI / numberSubBeams), color));
        }
    }

    public void update(float time, float yawAngleIncrease, float dt) {
        yawAngle += yawAngleIncrease;
        begin.set(1, 0, 0);
        begin.rotateRad(Vector3.Y, yawAngle);

        float length = 6;
        float pitch = (float) (0.35f + 0.25f * Math.cos(2 * time - phaseDifference));
        setAndRotateZY(tmpCorner, length, 0, 0, pitch);
        corner.set(begin).add(tmpCorner);

        float standardLength = 1.0f;
        setAndRotateZY(tmpEnd, length, standardLength, 0, pitch);
        end.set(begin).add(tmpEnd);

        for (SubBeam subBeam : subBeams) {
            subBeam.update(end, dt, pitch, yawAngle);
        }
    }

    public void draw(ShapeRenderer shape) {
        shape.line(begin.x, begin.y, begin.z, corner.x, corner.y, corner.z);
        shape.line(corner.x, corner.y, corner.z, end.x, end.y, end.z);

        shape.setColor(color);
        for (SubBeam subBeam : subBeams) {
            subBeam.draw(shape);
        }
        shape.setColor(Color.WHITE);
    }

    public Array<SubBeam> getSubBeams() {
        return subBeams;
    }

    public void setPhaseDifference(float phaseDifference) {
        this.phaseDifference = phaseDifference;
    }
}
