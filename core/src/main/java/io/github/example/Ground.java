package io.github.example;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;

public class Ground {
    private final ModelInstance ground;

    public Ground() {
        Model groundModel = new ModelBuilder().createCylinder(150f, 0.1f, 150f, 20,  // depth
            new Material(ColorAttribute.createDiffuse(new Color(0.1f, 0.55f, 0.15f, 1f))),
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);

        ground = new ModelInstance(groundModel);
        ground.transform.setToTranslation(0f, -2.1f, 0f);
    }

    public void render(ModelBatch modelBatch) {
        modelBatch.render(ground);
    }
}
