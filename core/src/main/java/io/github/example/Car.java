package io.github.example;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

public class Car extends Block {
    private final Model boxModel;

    public Car (Color color) {
        width = 1.3f;
        height = 1.0f;
        depth = 1.5f;
        ModelBuilder modelBuilder = new ModelBuilder();
        boxModel = modelBuilder.createBox(width, height, depth,
            new Material(ColorAttribute.createDiffuse(color)),
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal
        );

//        boxModel = modelBuilder.createCylinder(1.5f, 1.5f, 1.5f, 24, new Material(ColorAttribute.createDiffuse(color)),
//            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);

        box = new ModelInstance(boxModel);
    }

    public void update(Vector3 position, Vector3 root, float yawAngleDifference, float pitchBeam) {
        Vector3 direction = position.cpy().sub(root).nor();

        float horizontalLength = (float) Math.sqrt(direction.x * direction.x + direction.z * direction.z);
        float pitch = MathUtils.atan2(direction.y, horizontalLength);
        float yaw = MathUtils.atan2(direction.z, direction.x);

        box.transform.idt();
        box.transform.rotateRad(Vector3.Y, -yaw);
        box.transform.rotateRad(Vector3.Z, pitch);
        box.transform.rotateRad(Vector3.X, pitchBeam * (float) (Math.sin(yawAngleDifference)));
        box.transform.setTranslation(position);
    }

    public Model getBoxModel() {
        return boxModel;
    }

    public void render(ModelBatch modelBatch) {
        modelBatch.render(box);
    }

}
