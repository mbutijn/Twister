package io.github.example;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Quaternion;
import net.mgsx.gltf.loaders.glb.GLBAssetLoader;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneAsset;
import net.mgsx.gltf.scene3d.scene.SceneManager;

public class SubRotationSystem {
    private final Scene scene;
    private final SceneManager sceneManager;
    private float yawAngle;
    private float yawAngleDifference;
    private final Vector3 onRideCameraPosition = new Vector3();
    private final Vector3 seatPosition = new Vector3();
    private final Quaternion rot = new Quaternion();

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

        yawAngle = 0.25f * MathUtils.PI;
    }

    public void update(Vector3 position, float pitch, float yawAngleIncrease, float yawAngleBeam) {
        this.yawAngle += yawAngleIncrease;
        yawAngleDifference = yawAngleBeam - yawAngle;
        float pitchLocal = pitch * MathUtils.cos(yawAngleDifference);

        scene.modelInstance.transform.idt();
        scene.modelInstance.transform.setToTranslation(position);

        scene.modelInstance.transform.rotateRad(Vector3.Y, yawAngle);
        scene.modelInstance.transform.rotateRad(Vector3.Z, pitchLocal);
        scene.modelInstance.transform.rotateRad(Vector3.X, pitch * MathUtils.sin(yawAngleDifference));

        // update on-ride camera
        seatPosition.set(2.6f, 1.5f, 0f);
        scene.modelInstance.transform.getRotation(rot);

        rot.transform(seatPosition);
//        seatPosition.rotateRad(Vector3.Y, yawAngle);
//        seatPosition.rotateRad(Vector3.Z, pitchLocal);
//        seatPosition.rotateRad(Vector3.X, pitch * MathUtils.sin(yawAngleDifference));

        onRideCameraPosition.set(seatPosition.add(position));
    }

    public void render(PerspectiveCamera camera, Environment environment, float dt) {
        sceneManager.setCamera(camera);
        sceneManager.update(dt);
        sceneManager.render();
        sceneManager.environment = environment;
    }

    public Vector3 getOnRideCameraPosition() {
        return onRideCameraPosition;
    }

    public Quaternion getOnRideCameraRotation() {
        return rot.cpy();
    }

    public float getYawAngle() {
        return yawAngle;
    }

    public float getYawAngleDifference() {
        return yawAngleDifference;
    }
}
