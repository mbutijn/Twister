package io.github.example;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;

public class Beam extends Block {
    private final Vector3 begin = new Vector3();
    private final Vector3 corner = new Vector3();
    private final Vector3 end = new Vector3();
    private final Vector3 tmpCorner = new Vector3();
    private final Vector3 tmpEnd = new Vector3();
    private float phaseDifference;
    private final SubRotationSystem subRotationSystem;

    public Beam(float baseDirection, float phaseDifference, String color) {
        this.yawAngle = baseDirection;
        this.phaseDifference = phaseDifference;

        width = 6.0f;
        height = 0.4f;
        depth = 0.4f;
        ModelBuilder modelBuilder = new ModelBuilder();
        Model boxModel = modelBuilder.createBox(width, height, depth,
            new Material(ColorAttribute.createDiffuse(Color.GRAY)),
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal
        );

        box = new ModelInstance(boxModel);
        subRotationSystem = new SubRotationSystem(color);
    }

    public void update(float time, float yawAngleIncrease, float dt) {
        yawAngle += yawAngleIncrease;

        setAndRotateZY(begin, 1, 0, 0, 0);

        float length = 6;
        float pitch = (float) (0.35f + 0.25f * Math.cos(2 * time - phaseDifference));
        setAndRotateZY(tmpCorner, length, 0, 0, pitch);

        corner.set(begin).add(tmpCorner);

        box.transform.idt();
        box.transform.rotateRad(Vector3.Y, yawAngle);
        box.transform.rotateRad(Vector3.Z, pitch);
        box.transform.setTranslation(corner.cpy().add(begin).scl(0.5f));

        float standardLength = 0.2f;
        setAndRotateZY(tmpEnd, length, standardLength, 0, pitch);
        end.set(begin).add(tmpEnd);
        subRotationSystem.update(end, pitch, dt, yawAngle);
    }

    public void renderSubRotationSystem(PerspectiveCamera camera, float dt) {
        subRotationSystem.render(camera, dt);
    }

    public void draw(ModelBatch modelBatch) {
        modelBatch.render(box);
    }

    public void setPhaseDifference(float phaseDifference) {
        this.phaseDifference = phaseDifference;
    }

}
