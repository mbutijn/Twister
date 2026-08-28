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

public class Beam extends SimpleModel {
    private final Vector3 rootHinge = new Vector3();
    private final Vector3 corner = new Vector3();
    private final Vector3 end = new Vector3();
    private final Vector3 tmpCorner = new Vector3();
    private final Vector3 tmpEnd = new Vector3();
    private final Vector3 attachmentCylinder = new Vector3();

    public Beam() {
        width = 6.5f;
        height = 0.4f;
        depth = 0.4f;
        ModelBuilder modelBuilder = new ModelBuilder();
        Model boxModel = modelBuilder.createBox(width, height, depth,
            new Material(ColorAttribute.createDiffuse(Color.GRAY)),
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal
        );

        box = new ModelInstance(boxModel);
    }

    public void update(float yawAngle, float pitch) {
        setAndRotateZY(rootHinge, 1, 0, 0, 0, yawAngle);
        float length = 6;
        setAndRotateZY(tmpCorner, length, 0, 0, pitch, yawAngle);

        corner.set(rootHinge).add(tmpCorner);
        box.transform.idt();
        box.transform.rotateRad(Vector3.Y, yawAngle);
        box.transform.rotateRad(Vector3.Z, pitch);
        box.transform.setTranslation(corner.cpy().add(rootHinge).scl(0.5f));

        float standardLength = 0.2f;
        setAndRotateZY(tmpEnd, length, standardLength, 0, pitch, yawAngle);
        end.set(rootHinge).add(tmpEnd);

        setAndRotateZY(attachmentCylinder, 0, -0.25f, 0, pitch, yawAngle);
    }

    public void draw(ModelBatch modelBatch) {
        modelBatch.render(box);
    }

    public Vector3 getEnd() {
        return end;
    }

    public Vector3 getAttachment() {
        return corner.cpy().sub(rootHinge).scl(0.75f).add(attachmentCylinder);
    }

    public Vector3 getRootHinge() {
        return rootHinge;
    }
}
