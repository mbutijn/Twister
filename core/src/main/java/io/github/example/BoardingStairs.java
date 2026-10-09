package io.github.example;

import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

public class BoardingStairs {
    private final Array<BoardingStep> steps = new Array<>();

    public BoardingStairs(int x, int z) {
        float angle = 0.25f * MathUtils.PI * x * z;

        steps.add(new BoardingStep(5f, 0.66f, angle, 0.34f, x, z));
        steps.add(new BoardingStep(4.5f, 1.33f, angle, 0.67f, x, z));
        steps.add(new BoardingStep(4f, 2.0f, angle, 1.0f, x, z));
    }

    public void update(float progress) {
        for (BoardingStep step : steps) {
            step.update(progress);
        }
    }

    public void draw(ModelBatch modelBatch, Environment environment) {
        for (BoardingStep step : steps) {
            step.draw(modelBatch, environment);
        }
    }
}
