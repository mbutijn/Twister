package io.github.example;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;

public class Beam extends SimpleModel {
    private final Vector3 rootHinge = new Vector3();
    private final Vector3 corner = new Vector3();
    private final Vector3 end = new Vector3();
    private final Vector3 tmpCorner = new Vector3();
    private final Vector3 tmpEnd = new Vector3();
    private final Vector3 offsetCylinder = new Vector3();
    private final Vector3 attachmentCylinder = new Vector3();
    private final Hinge cylinder, root;

    public Beam() {
        width = 7.0f;
        height = 0.4f;
        depth = 0.4f;
        ModelBuilder modelBuilder = new ModelBuilder();
        Model boxModel = modelBuilder.createBox(width, height, depth,
            new Material(ColorAttribute.createDiffuse(Color.GRAY)),
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal
        );

        box = new ModelInstance(boxModel);
        cylinder = new Hinge(0.4f, 0.4f, 0.4f, Color.GRAY);
        root = new Hinge(0.7f, 0.7f, 0.7f, Color.DARK_GRAY);
    }

    public void update(float yawAngle, float pitch) {
        setAndRotateZY(rootHinge, 1, 0, 0, 0, yawAngle);
        float length = 6.5f;
        setAndRotateZY(tmpCorner, length, 0, 0, pitch, yawAngle);

        corner.set(rootHinge).add(tmpCorner);
        box.transform.idt();
        box.transform.rotateRad(Vector3.Y, yawAngle);
        box.transform.rotateRad(Vector3.Z, pitch);
        box.transform.setTranslation(corner.cpy().add(rootHinge).scl(0.5f));

        float standardLength = 0.2f;
        setAndRotateZY(tmpEnd, length, standardLength, 0, pitch, yawAngle);
        end.set(rootHinge).add(tmpEnd);

        setAndRotateZY(offsetCylinder, 0, -0.25f, 0, pitch, yawAngle);
        attachmentCylinder.set(corner.cpy().sub(rootHinge).scl(0.75f).add(offsetCylinder));

        cylinder.update(yawAngle, pitch, attachmentCylinder.cpy().add(rootHinge));
        root.update(yawAngle, pitch, rootHinge);
    }

    public void draw(ModelBatch modelBatch, Environment environment) {
        modelBatch.render(box, environment);
        cylinder.render(modelBatch, environment);
        root.render(modelBatch, environment);
    }

    public Vector3 getEnd() {
        return end;
    }

    public Vector3 getAttachment() {
        return attachmentCylinder;
    }

    public Vector3 getRootHinge() {
        return rootHinge;
    }
}
