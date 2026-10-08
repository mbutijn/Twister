package io.github.example;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

public class BoardingStep {
    private final ModelInstance modelInstanceLow, modelInstanceMid, modelInstanceHigh;
    private final Vector3 positionLow, positionMid, positionHigh;
    private final float angle;

    public BoardingStep(int x, int z) {
        angle = 0.25f * MathUtils.PI * x * z;
        ModelBuilder modelBuilder = new ModelBuilder();
        positionLow = new Vector3().set(x * 5f, -2.0f, z * 5f);
        positionMid = new Vector3().set(x * 4.5f, -2.0f, z * 4.5f);
        positionHigh = new Vector3().set(x * 4f, -2.0f, z * 4f);

        Model stepLow = modelBuilder.createBox(2.6f, 0.66f, 1,
            new Material(ColorAttribute.createDiffuse(new Color(Color.DARK_GRAY))),
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal
        );
        Model stepMid = modelBuilder.createBox(2.6f, 1.33f, 1,
            new Material(ColorAttribute.createDiffuse(new Color(Color.DARK_GRAY))),
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal
        );
        Model stepHigh = modelBuilder.createBox(2.6f, 2.0f, 1,
            new Material(ColorAttribute.createDiffuse(new Color(Color.DARK_GRAY))),
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal
        );

        modelInstanceLow = new ModelInstance(stepLow);
        modelInstanceLow.transform.idt();
        modelInstanceLow.transform.setTranslation(new Vector3().set(x * 5f, -1.66f, z * 5f));
        modelInstanceLow.transform.rotateRad(Vector3.Y, angle);

        modelInstanceMid = new ModelInstance(stepMid);
        modelInstanceMid.transform.idt();
        modelInstanceMid.transform.setTranslation(new Vector3().set(x * 4.5f, -1.33f, z * 4.5f));
        modelInstanceMid.transform.rotateRad(Vector3.Y, angle);

        modelInstanceHigh = new ModelInstance(stepHigh);
        modelInstanceHigh.transform.idt();
        modelInstanceHigh.transform.setTranslation(new Vector3().set(x * 4f, -1.0f, z * 4f));
        modelInstanceHigh.transform.rotateRad(Vector3.Y, angle);
    }

    public void update(float progress) {
        modelInstanceLow.transform.idt();
        modelInstanceMid.transform.idt();
        modelInstanceHigh.transform.idt();
        modelInstanceLow.transform.scale(1, progress, 1);
        modelInstanceMid.transform.scale(1, progress, 1);
        modelInstanceHigh.transform.scale(1, progress, 1);

        modelInstanceLow.transform.setTranslation(positionLow.cpy().add(0, 0.34f * progress, 0));
        modelInstanceMid.transform.setTranslation(positionMid.cpy().add(0, 0.67f * progress, 0));
        modelInstanceHigh.transform.setTranslation(positionHigh.cpy().add(0, progress, 0));

        modelInstanceLow.transform.rotateRad(Vector3.Y, angle);
        modelInstanceMid.transform.rotateRad(Vector3.Y, angle);
        modelInstanceHigh.transform.rotateRad(Vector3.Y, angle);
    }

    public void draw(ModelBatch modelBatch, Environment environment) {
        modelBatch.render(modelInstanceLow, environment);
        modelBatch.render(modelInstanceMid, environment);
        modelBatch.render(modelInstanceHigh, environment);
    }
}
