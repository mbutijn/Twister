package io.github.example;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Arm {
    private final Array <LinePiece> linePieces = new Array<>();
    private final int numberOfLinePieces;

    public Arm(float x, float y, float baseDirection) {
        numberOfLinePieces = 20;
        linePieces.add(new LinePiece(new Vector2(x, y), baseDirection, 0));
        for (int i = 1; i < numberOfLinePieces; i++) {
            linePieces.add(new LinePiece(linePieces.get(i - 1).getVector2(), baseDirection, i));
        }
    }

    public void update(long time, float dt) {
        linePieces.get(0).update(time);
        for (int i = 1; i < numberOfLinePieces; i++) {
            LinePiece linePiece = linePieces.get(i);
            linePiece.updateWithV1(linePieces.get(i - 1).getVector2(), time);
        }
    }

    public void draw(ShapeRenderer shape) {
        for (LinePiece linePiece : linePieces) {
            if (linePiece.getIndex() != 0) {
                linePiece.draw(shape);
            }
        }
    }

    public Array<LinePiece> getLinePieces() {
        return linePieces;
    }

}
