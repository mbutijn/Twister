package io.github.example;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;

public class Root extends Block {
    private float rotation;

    public Root() {
        ModelBuilder modelBuilder = new ModelBuilder();
        width = 1.5f;
        height = 2f;
        depth = 1.5f;
        Model boxModel = modelBuilder.createBox(width, height, depth,
            new Material(ColorAttribute.createDiffuse(Color.DARK_GRAY)),
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal
        );

        box = new ModelInstance(boxModel);
        //rotation = (float) (0.25 * Math.PI);
    }

    public void update(float rotationIncrease) {
        rotation += rotationIncrease;
        box.transform.idt();
        box.transform.rotateRad(Vector3.Y, rotation);
        box.transform.setTranslation(0, -1, 0);
    }

    public void render(ModelBatch modelBatch){
        modelBatch.render(box);
    }
}
