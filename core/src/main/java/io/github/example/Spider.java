package io.github.example;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Spider extends ApplicationAdapter implements InputProcessor {
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private ShapeRenderer shape;
    private SpriteBatch batch;
    private Texture image;
    private Array<Arm> arms;
    private float xCenter, yCenter;
    private static double lineVelocity = 0.001f;
    private static float baseVelocity = 0.01f;
    private long time;

    public Spider() {
        camera = new OrthographicCamera();

        camera.position.x = 512;
        camera.position.y = 384;
        camera.position.z = 100;
        viewport = new FitViewport(1024, 768, camera);
    }

    public static double getLineVelocity() {
        return lineVelocity;
    }

    public static float getBaseVelocity() {
        return baseVelocity;
    }

    @Override
    public void create() {
        batch = new SpriteBatch();

        shape = new ShapeRenderer();
        shape.setColor(Color.WHITE);
        image = new Texture("libgdx.png");

        xCenter = 0.5f * viewport.getWorldWidth();
        yCenter = 0.5f * viewport.getWorldHeight();

        // Set up the input processor
        Gdx.input.setInputProcessor(this);

        int numberOfArms = 20;
        arms = new Array<>();
        for (int i = 0; i < numberOfArms; i++) {
            arms.add(new Arm(xCenter, yCenter, (float) (i * 2 * Math.PI / numberOfArms)));
        }
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        batch.begin();
        batch.draw(image, 140, 210);
        batch.end();

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        viewport.apply();

        shape.begin(ShapeRenderer.ShapeType.Line);
        shape.setProjectionMatrix(camera.combined);
        shape.circle(xCenter, yCenter, 20);
        shape.end();

        shape.begin(ShapeRenderer.ShapeType.Filled);
        time = System.currentTimeMillis();
        float dt = Gdx.graphics.getDeltaTime();

        for (Arm arm : arms) {
            arm.update(time, dt);
            arm.draw(shape);
        }

        shape.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        image.dispose();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.PAGE_DOWN) {
            lineVelocity -= 0.0001;
            baseVelocity -= 0.001f;
            updatePhaseAngles();
        }
        if (keycode == Input.Keys.PAGE_UP) {
            lineVelocity += 0.0001;
            baseVelocity += 0.001f;
            updatePhaseAngles();
        }
        return false;
    }

    public void updatePhaseAngles() {
        for (Arm arm : arms) {
            for (LinePiece linePiece : arm.getLinePieces()) {
                linePiece.updatePhaseDifference(time);
            }
        }
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
