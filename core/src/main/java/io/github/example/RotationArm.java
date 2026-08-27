package io.github.example;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.ModelBatch;

public class RotationArm {
    private final SubRotationSystem subRotationSystem;
    private final HydraulicCylinder hydraulicCylinder;
    private final Beam beam;
    private float yawAngle;
    private float phaseDifference;

    public RotationArm(float yawAngle, float phaseDifference, String color) {
        this.yawAngle = yawAngle;
        this.phaseDifference = phaseDifference;
        this.beam = new Beam();
        this.subRotationSystem = new SubRotationSystem(color);
        this.hydraulicCylinder = new HydraulicCylinder();
    }

    public void update(float time, float yawAngleIncrease, float dt) {
        yawAngle += yawAngleIncrease;
        float pitch = (float) (0.35f + 0.25f * Math.cos(2 * time - phaseDifference));
        beam.update(yawAngle, pitch);
        subRotationSystem.update(beam.getEnd(), pitch, dt, yawAngle);

        hydraulicCylinder.update(yawAngle, beam.getAttachment(), beam.getBegin());
    }

    public void drawHydraulics(ModelBatch modelBatch) {
        hydraulicCylinder.draw(modelBatch);
    }

    public void renderSubRotationSystem(PerspectiveCamera camera, float dt) {
        subRotationSystem.render(camera, dt);
    }

    public Beam getBeam() {
        return beam;
    }

    public void setPhaseDifference(float phaseDifference) {
        this.phaseDifference = phaseDifference;
    }

}
