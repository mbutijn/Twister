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
    private final Color nightColor = new Color(0.05f, 0.25f, 0.05f, 1f);
    private final Color dayColor = new Color(0.15f, 0.55f, 0.15f, 1f);

    public Ground() {
        Model groundModel = new ModelBuilder().createCylinder(150f, 0.1f, 150f, 20,  // depth
            new Material(ColorAttribute.createDiffuse(nightColor)),
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);

        ground = new ModelInstance(groundModel);
        ground.transform.setToTranslation(0f, -2.1f, 0f);
    }

    public void switchDayNight(boolean isDay) {
        ground.materials.get(0).set(ColorAttribute.createDiffuse(isDay ? dayColor : nightColor));
    }

    public void render(ModelBatch modelBatch) {
        modelBatch.render(ground);
    }
}
