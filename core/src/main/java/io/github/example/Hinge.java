package io.github.example;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

public class Hinge {
    private final ModelInstance pin;
    private final ModelInstance leftRing;
    private final ModelInstance rightRing;
    private final Vector3 leftOffset = new Vector3(0, 0, 0);
    private final Vector3 rightOffset = new Vector3(0, 0, 0);
    private final float height;

    public Hinge(float radius, float height, float ringThickness, Color color) {
        this.height = height;
        this.pin = makeModelInstance(radius, height, color);
        this.leftRing = makeModelInstance(1.2f * radius, ringThickness, color);
        this.rightRing = makeModelInstance(1.2f * radius, ringThickness, color);
    }

    public ModelInstance makeModelInstance(float radius, float height, Color color) {
        ModelBuilder modelBuilder = new ModelBuilder();
        Model model = modelBuilder.createCylinder(radius, height, radius, 16,
            new Material(ColorAttribute.createDiffuse(color)),
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal
        );

        return new ModelInstance(model);
    }

    public void update(float yawAngle, float pitch, Vector3 position) {
        updateOrientation(leftRing, yawAngle, pitch);
        leftOffset.set(0, 0, 0.5f * height);
        leftOffset.rotateRad(Vector3.Y, yawAngle);
        leftRing.transform.setTranslation(position.cpy().add(leftOffset));

        updateOrientation(rightRing, yawAngle, pitch);
        rightOffset.set(0, 0, -0.5f * height);
        rightOffset.rotateRad(Vector3.Y, yawAngle);
        rightRing.transform.setTranslation(position.cpy().add(rightOffset));

        updateOrientation(pin, yawAngle, pitch);
        pin.transform.setTranslation(position);
    }

    public void updateOrientation(ModelInstance modelInstance, float yawAngle, float pitch) {
        modelInstance.transform.idt();
        modelInstance.transform.rotateRad(Vector3.Y, yawAngle);
        modelInstance.transform.rotateRad(Vector3.Z, pitch);
        modelInstance.transform.rotateRad(Vector3.X, 0.5f * MathUtils.PI);
    }

    public void render(ModelBatch modelBatch, Environment environment) {
        modelBatch.render(leftRing, environment);
        modelBatch.render(rightRing, environment);
        modelBatch.render(pin, environment);
    }
}
