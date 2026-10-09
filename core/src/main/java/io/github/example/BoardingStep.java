package io.github.example;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;

public class BoardingStep {
    private final ModelInstance modelInstance;
    private final Vector3 position;
    private final float angle;
    private final float speed;

    public BoardingStep(float distance, float height, float angle, float speed, int x, int z) {
        this.angle = angle;
        this.speed = speed;
        this.position = new Vector3().set(x * distance, -2.0f, z * distance);

        ModelBuilder modelBuilder = new ModelBuilder();
        Model step = modelBuilder.createBox(2.7f, height, 1,
            new Material(ColorAttribute.createDiffuse(new Color(Color.DARK_GRAY))),
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal
        );

        modelInstance = new ModelInstance(step);
    }

    public void update(float progress) {
        modelInstance.transform.idt();
        modelInstance.transform.scale(1, progress, 1);
        modelInstance.transform.setTranslation(position.cpy().add(0, speed * progress, 0));
        modelInstance.transform.rotateRad(Vector3.Y, angle);
    }

    public void draw(ModelBatch modelBatch, Environment environment) {
        modelBatch.render(modelInstance, environment);
    }
}
