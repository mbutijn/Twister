package io.github.example;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.math.MathUtils;

public class Light {
    private final PointLight pointLight1;
    private final Color lightColor;

    public Light(Environment environment) {
        DirectionalLight sun = new DirectionalLight();
        sun.set(Color.WHITE, // light color
            0, -1f, 0); // direction
        sun.color.set(10f, 10f, 10f, 1f);
        lightColor = new Color(1f, 1f, 1f, 1f);
        pointLight1 = new PointLight();
        PointLight pointLight2 = new PointLight();
        pointLight1.set(lightColor, 10, 5f, 0, 250f);
        pointLight2.set(lightColor, -10, 5f, 0, 250f);

        environment.add(sun);
        environment.add(pointLight1);
        environment.add(pointLight2);
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.2f, 0.2f, 0.2f, 1f));
    }

    public void update(float time) { // only in nighttime
        lightColor.set(0.5f + 0.5f * MathUtils.cos(6 * time),
            0.6f + 0.4f * MathUtils.cos(4 * time),
            0.5f + 0.5f * MathUtils.sin(2 * time), 1f);
        pointLight1.setColor(lightColor);
        pointLight1.intensity = 600f + 300f * MathUtils.cos(5 * time);
    }

    public void switchDayNight(boolean isDay) {
        if (isDay) {
            pointLight1.setIntensity(250f);
            pointLight1.setColor(Color.WHITE);
        }
    }
}
