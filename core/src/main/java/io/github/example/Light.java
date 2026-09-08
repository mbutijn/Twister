package io.github.example;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.math.MathUtils;

public class Light {
    private final PointLight pointLight;
    private final Color lightColor;
    private final DirectionalLight sun;

    public Light(Environment environment) {
        sun = new DirectionalLight();
        sun.set(Color.WHITE, // light color
            -1f, -1f, -1f); // direction
        lightColor = new Color(1f, 1f, 1f, 1f);
        pointLight = new PointLight();
        pointLight.set(lightColor, 10, 5f, 0, 500f);

        environment.add(sun);
        environment.add(pointLight);
    }

    public void update(float time) {
        lightColor.set(0.5f + 0.5f * MathUtils.cos(6 * time),
            0.6f + 0.4f * MathUtils.cos(4 * time),
            0.5f + 0.5f * MathUtils.sin(2 * time), 1f);
        pointLight.setColor(lightColor);
        pointLight.intensity = 600f + 300f * MathUtils.cos(5 * time);
    }

    public void switchDayNight(boolean isDay) {
        if (isDay) {
            pointLight.setColor(Color.WHITE);
        } else {
            pointLight.setIntensity(0f);
        }
    }
}
