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

    public void update(float time, float yawAngleIncrease, float pitchAmplitude) {
        yawAngle += yawAngleIncrease;
        //float pitch = (float) (0.2f + 0.4f * Math.cos(2 * time - phaseDifference));
        float pitch = (float) (pitchAmplitude - 0.2f + pitchAmplitude * Math.cos(2 * time - phaseDifference)); // min = -0.2, max = 0.6
        beam.update(yawAngle, pitch);
        subRotationSystem.update(beam.getEnd(), pitch, 2 * yawAngleIncrease, yawAngle);

        hydraulicCylinder.update(yawAngle, beam.getAttachment(), beam.getRootHinge());
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
