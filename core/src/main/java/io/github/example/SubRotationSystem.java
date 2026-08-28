package io.github.example;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;
import net.mgsx.gltf.loaders.glb.GLBAssetLoader;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneAsset;
import net.mgsx.gltf.scene3d.scene.SceneManager;

public class SubRotationSystem {
    private final Scene scene;
    private final SceneManager sceneManager;
    private float yawAngle;

    public SubRotationSystem (String color) {
        AssetManager assetManager = new AssetManager();
        assetManager.setLoader(
            SceneAsset.class,
            ".glb",
            new GLBAssetLoader()
        );

        String fileName = "car_" + color + ".glb";
        assetManager.load(fileName, SceneAsset.class);
        assetManager.finishLoading();
        SceneAsset sceneAsset = assetManager.get(fileName, SceneAsset.class);

        scene = new Scene(sceneAsset.scene);
        sceneManager = new SceneManager();
        sceneManager.addScene(scene);

        yawAngle = 0;
    }

    public void update(Vector3 position, float pitch, float yawAngleIncrease, float yawAngleBeam) {
        this.yawAngle += yawAngleIncrease;
        float yawAngleDifference = yawAngleBeam - yawAngle;
        float pitchLocal = (float) (pitch * Math.cos(yawAngleDifference));

        scene.modelInstance.transform.idt();
        scene.modelInstance.transform.setToTranslation(position);

        scene.modelInstance.transform.rotateRad(Vector3.Y, yawAngle);
        scene.modelInstance.transform.rotateRad(Vector3.Z, pitchLocal);
        scene.modelInstance.transform.rotateRad(Vector3.X, pitch * (float) (Math.sin(yawAngleDifference)));
    }

    public void render(PerspectiveCamera camera, float dt) {
        sceneManager.setCamera(camera);
        sceneManager.update(dt);
        sceneManager.render();
    }

}
