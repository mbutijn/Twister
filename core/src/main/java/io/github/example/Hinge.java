package io.github.example;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

public class Hinge {
    private final ModelInstance modelInstance;

    public Hinge(float width, float height, float depth, Color color) {
        ModelBuilder modelBuilder = new ModelBuilder();
        Model model = modelBuilder.createCylinder(width, height, depth, 16,
            new Material(ColorAttribute.createDiffuse(color)),
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal
        );

        this.modelInstance = new ModelInstance(model);
    }

    public void update(float yawAngle, float pitch, Vector3 position) {
        modelInstance.transform.idt();
        modelInstance.transform.rotateRad(Vector3.Y, yawAngle);
        modelInstance.transform.rotateRad(Vector3.Z, pitch);
        modelInstance.transform.rotateRad(Vector3.X, 0.5f * MathUtils.PI);
        modelInstance.transform.setTranslation(position);
    }

    public void render(ModelBatch modelBatch, Environment environment) {
        modelBatch.render(modelInstance, environment);
    }
}
