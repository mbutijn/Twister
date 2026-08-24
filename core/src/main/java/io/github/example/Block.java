package io.github.example;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;

public class Block {
    protected ModelInstance box;
    protected float width, height, depth;

    public void drawBoxEdges(ShapeRenderer shapeRenderer) {
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);

        float sx = width / 2f;
        float sy = height / 2f;
        float sz = depth / 2f;

        Vector3[] corners = {
            new Vector3(-sx, -sy, -sz),
            new Vector3( sx, -sy, -sz),
            new Vector3( sx,  sy, -sz),
            new Vector3(-sx,  sy, -sz),

            new Vector3(-sx, -sy,  sz),
            new Vector3( sx, -sy,  sz),
            new Vector3( sx,  sy,  sz),
            new Vector3(-sx,  sy,  sz)
        };

        // Transform local coordinates into world coordinates
        for (Vector3 corner : corners) {
            corner.mul(box.transform);
        }

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        // Bottom
        shapeRenderer.line(corners[0], corners[1]);
        shapeRenderer.line(corners[1], corners[2]);
        shapeRenderer.line(corners[2], corners[3]);
        shapeRenderer.line(corners[3], corners[0]);

        // Top
        shapeRenderer.line(corners[4], corners[5]);
        shapeRenderer.line(corners[5], corners[6]);
        shapeRenderer.line(corners[6], corners[7]);
        shapeRenderer.line(corners[7], corners[4]);

        // Vertical edges
        shapeRenderer.line(corners[0], corners[4]);
        shapeRenderer.line(corners[1], corners[5]);
        shapeRenderer.line(corners[2], corners[6]);
        shapeRenderer.line(corners[3], corners[7]);

        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
    }
}
