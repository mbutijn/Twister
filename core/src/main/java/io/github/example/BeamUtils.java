package io.github.example;

import com.badlogic.gdx.math.Vector3;

public class BeamUtils {
    protected float yawAngle;

    public void setAndRotateZY(Vector3 vector3, float x, float y, float z, float angle) {
        vector3.set(x, y, z);
        vector3.rotateRad(Vector3.Z, angle);
        vector3.rotateRad(Vector3.Y, yawAngle);
    }
}
