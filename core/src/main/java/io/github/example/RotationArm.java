package io.github.example;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.MathUtils;

public class RotationArm {
    private final SubRotationSystem subRotationSystem;
    private final HydraulicCylinder hydraulicCylinder;
    private final Beam beam;
    private float yawAngle;
    private float phaseDifference;

    public RotationArm(float yawAngle, float phaseDifference, String colorString, Color color) {
        this.yawAngle = yawAngle;
        this.phaseDifference = phaseDifference;
        this.beam = new Beam(color);
        this.subRotationSystem = new SubRotationSystem(colorString);
        this.hydraulicCylinder = new HydraulicCylinder(color);
    }

    public void update(float time, float yawAngleIncrease, float rotationSpeedSub, float pitchEquilibrium, float pitchAmplitude) {
        yawAngle += yawAngleIncrease;
        float pitch = pitchEquilibrium + pitchAmplitude * MathUtils.cos(2 * time - phaseDifference); // min = -0.2, max = 0.6
        beam.update(yawAngle, pitch);
        subRotationSystem.update(beam.getEnd(), pitch, yawAngleIncrease + rotationSpeedSub, yawAngle);
        hydraulicCylinder.update(yawAngle, beam.getAttachment(), beam.getRootHinge());
    }

    public void drawHydraulics(ModelBatch modelBatch, Environment environment) {
        hydraulicCylinder.draw(modelBatch, environment);
    }

    public void renderSubRotationSystem(PerspectiveCamera camera, Environment environment, float dt) {
        subRotationSystem.render(camera, environment, dt);
    }

    public Beam getBeam() {
        return beam;
    }

    public void setPhaseDifference(float phaseDifference) {
        this.phaseDifference = phaseDifference;
    }

    public SubRotationSystem getSubRotationSystem() {
        return subRotationSystem;
    }

    public float getYawAngle() {
        return yawAngle;
    }
}
