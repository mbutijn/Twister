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

public class HydraulicCylinder {
    private final ModelInstance cylinderDown, cylinderUp;
    private final Vector3 attachmentRoot = new Vector3();
    private final Vector3 midPointUpCylinder = new Vector3();

    public HydraulicCylinder() {
        ModelBuilder modelBuilder = new ModelBuilder();

        Model cylinderLow = modelBuilder.createCylinder(0.4f, 2f, 0.4f, 24,
            new Material(ColorAttribute.createDiffuse(Color.GRAY)),
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal
        );
        Model cylinderHigh = modelBuilder.createCylinder(0.2f, 4f, 0.2f, 24,
            new Material(ColorAttribute.createDiffuse(Color.LIGHT_GRAY)),
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal
        );

        cylinderDown = new ModelInstance(cylinderLow);
        cylinderUp = new ModelInstance(cylinderHigh);
    }

    public void update(float yawAngle, Vector3 beamAttachment, Vector3 begin) {
        setAndRotateZY(attachmentRoot, 0, -1.5f, 0, 0, yawAngle);

        Vector3 hydraulicVector = beamAttachment.cpy().sub(attachmentRoot);
        Vector3 hydraulicNor = hydraulicVector.nor();
        float pitch = (float) Math.asin(hydraulicNor.y);

        cylinderDown.transform.idt();
        cylinderDown.transform.translate(hydraulicNor.scl(1.0f).add(begin).add(0,-1.5f,0));

        cylinderDown.transform.rotateRad(Vector3.Y, yawAngle);
        cylinderDown.transform.rotateRad(Vector3.Z, (float) (pitch - 0.5 * Math.PI));

        cylinderUp.transform.idt();
        midPointUpCylinder.set(0, 2f, 0);

        midPointUpCylinder.rotateRad(Vector3.Z, (float) (pitch - 0.5 * Math.PI));
        midPointUpCylinder.rotateRad(Vector3.Y, yawAngle);

        cylinderUp.transform.translate(beamAttachment.add(begin).sub(midPointUpCylinder));
        cylinderUp.transform.rotateRad(Vector3.Y, yawAngle);
        cylinderUp.transform.rotateRad(Vector3.Z, (float) (pitch - 0.5 * Math.PI));
    }

    public void setAndRotateZY(Vector3 vector3, float x, float y, float z, float angleZ, float angleY) {
        vector3.set(x, y, z);
        vector3.rotateRad(Vector3.Z, angleZ);
        vector3.rotateRad(Vector3.Y, angleY);
    }

    public void draw(ModelBatch modelBatch) {
        modelBatch.render(cylinderDown);
        modelBatch.render(cylinderUp);
    }
}
