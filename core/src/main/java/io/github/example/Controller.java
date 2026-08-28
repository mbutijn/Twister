package io.github.example;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class Controller {
    private final Stage stage;
    private final Slider speedSlider, pitchAmplitudeSlider;
    private float targetRotationSpeed, targetPitchAmplitude;

    public Controller() {
        stage = new Stage();
        Table table = new Table();
        table.setFillParent(true);
        table.row();

        Drawable background = new TextureRegionDrawable(
            new TextureRegion(new Texture("ui/slider-background.png"))
        );
        Drawable knob = new TextureRegionDrawable(
            new TextureRegion(new Texture("ui/slider-knob.png"))
        );

        Slider.SliderStyle sliderStyle = new Slider.SliderStyle();
        sliderStyle.background = background;
        sliderStyle.knob = knob;

        speedSlider = new Slider(0, 1.2f,  0.05f, true, sliderStyle);
        speedSlider.setWidth(30);
        speedSlider.setHeight(300);
        speedSlider.setPosition(30, 250);
        speedSlider.setValue(0.75f);
        targetRotationSpeed = 0.75f;

        speedSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                targetRotationSpeed = speedSlider.getValue();
            }
        });

        pitchAmplitudeSlider = new Slider(0, 0.4f,  0.01f, true, sliderStyle);
        pitchAmplitudeSlider.setWidth(30);
        pitchAmplitudeSlider.setHeight(300);
        pitchAmplitudeSlider.setPosition(30, 250);
        pitchAmplitudeSlider.setValue(0.4f);
        targetPitchAmplitude = 0.4f;

        pitchAmplitudeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                targetPitchAmplitude = pitchAmplitudeSlider.getValue();
            }
        });

        table.left().bottom().pad(20);
        table.add(speedSlider).width(20).pad(50);
        table.add(pitchAmplitudeSlider).width(10).pad(1);

        stage.addActor(table);
    }

    public void update(float delta) {
        stage.act(delta);
    }

    public void render() {
        stage.draw();
    }

    public Stage getStage() {
        return stage;
    }

    public float getTargetRotationSpeed() {
        return targetRotationSpeed;
    }

    public float getTargetPitchAmplitude() {
        return targetPitchAmplitude;
    }
}
