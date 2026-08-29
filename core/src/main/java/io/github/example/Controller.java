package io.github.example;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class Controller {
    private final Stage stage;
    private final Slider rotationSpeedMainSlider, rotationSpeedSubSlider, pitchEquilibriumSlider, maxPhaseDifferenceSlider;
    private final LimitedSlider pitchAmplitudeSlider;
    private float rotationSpeedMain, rotationSpeedSub, pitchEquilibrium, pitchAmplitude, maxPhaseDifference;
    private float targetRotationSpeedMain, targetRotationSpeedSub, targetPitchEquilibrium, targetPitchAmplitude, targetMaxPhaseDifference;

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

        rotationSpeedMainSlider = new Slider(0, 1.2f, 0.05f, true, sliderStyle);
        addDimensions(rotationSpeedMainSlider);
        rotationSpeedMainSlider.setValue(0.75f);
        targetRotationSpeedMain = 0.75f;
        rotationSpeedMainSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                targetRotationSpeedMain = rotationSpeedMainSlider.getValue();
            }
        });

        rotationSpeedSubSlider = new Slider(0, 2f, 0.1f, true, sliderStyle);
        addDimensions(rotationSpeedSubSlider);
        rotationSpeedSubSlider.setValue(1.2f);
        targetRotationSpeedSub = 1.2f;
        rotationSpeedSubSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                targetRotationSpeedSub = rotationSpeedSubSlider.getValue();
            }
        });

        pitchEquilibriumSlider = new Slider(-0.2f, 0.6f, 0.01f, true, sliderStyle);
        addDimensions(pitchEquilibriumSlider);
        pitchEquilibriumSlider.setValue(0.2f);
        targetPitchEquilibrium = 0.2f;
        pitchEquilibriumSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                targetPitchEquilibrium = pitchEquilibriumSlider.getValue();
                updateAmplitudeRange();
            }
        });

        pitchAmplitudeSlider = new LimitedSlider(0, 0.4f, 0.01f, true, sliderStyle);
        addDimensions(pitchAmplitudeSlider);
        pitchAmplitudeSlider.setValue(0.4f);
        targetPitchAmplitude = 0.4f;
        pitchAmplitudeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                targetPitchAmplitude = pitchAmplitudeSlider.getValue();
            }
        });

        maxPhaseDifferenceSlider = new Slider(0, 4f * MathUtils.PI, 0.5f * MathUtils.PI, true, sliderStyle);
        addDimensions(maxPhaseDifferenceSlider);
        maxPhaseDifferenceSlider.setValue(0.5f * MathUtils.PI);
        targetMaxPhaseDifference = 0.5f * MathUtils.PI;
        maxPhaseDifferenceSlider.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                targetMaxPhaseDifference = maxPhaseDifferenceSlider.getValue();
            }
        });

        table.left().bottom().pad(20);
        table.add(rotationSpeedMainSlider).width(10).pad(15);
        table.add(rotationSpeedSubSlider).width(10).pad(15);
        table.add(pitchEquilibriumSlider).width(10).pad(15);
        table.add(pitchAmplitudeSlider).width(10).pad(15);
        table.add(maxPhaseDifferenceSlider).width(10).pad(15);

        stage.addActor(table);

        rotationSpeedMain = targetRotationSpeedMain;
        rotationSpeedSub = targetRotationSpeedSub;
        pitchEquilibrium = targetPitchEquilibrium;
        pitchAmplitude = targetPitchAmplitude;
        maxPhaseDifference = targetMaxPhaseDifference;
    }

    private void addDimensions(Slider slider) {
        slider.setWidth(30);
        slider.setHeight(300);
        slider.setPosition(30, 250);
    }

    private void addListenerToSlider(Slider slider){

    }

    private void updateAmplitudeRange() {
        float pitchEquilibrium = pitchEquilibriumSlider.getValue();
        float distanceToMin = pitchEquilibrium - pitchEquilibriumSlider.getMinValue();
        float distanceToMax = pitchEquilibriumSlider.getMaxValue() - pitchEquilibrium;

        pitchAmplitudeSlider.setEffectiveMax(Math.min(distanceToMin, distanceToMax));
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

    public void updateTimeDependentValues() {
        rotationSpeedMain = changeValue(rotationSpeedMain, targetRotationSpeedMain, 0.005f);
        rotationSpeedSub = changeValue(rotationSpeedSub, targetRotationSpeedSub, 0.008f);
        pitchEquilibrium = changeValue(pitchEquilibrium, targetPitchEquilibrium, 0.004f);
        pitchAmplitude = changeValue(pitchAmplitude, targetPitchAmplitude, 0.005f);
        maxPhaseDifference = changeValue(maxPhaseDifference, targetMaxPhaseDifference, 0.01f);
    }

    private float changeValue(float value, float targetValue, float increment) {
        float difference = targetValue - value;
        if (difference > 0) {
            value = difference > increment ? value + increment : targetValue;
        }
        if (difference < 0) {
            value = difference < -increment ? value - increment : targetValue;
        }
        return  value;
    }

    public float getRotationSpeedMain() {
        return rotationSpeedMain;
    }

    public float getRotationSpeedSub() {
        return rotationSpeedSub;
    }

    public float getPitchEquilibrium() {
        return pitchEquilibrium;
    }

    public float getPitchAmplitude() {
        return pitchAmplitude;
    }

    public float getMaxPhaseDifference() {
        return maxPhaseDifference;
    }

    public boolean isPhaseDifferenceNeedsChange() {
        return targetMaxPhaseDifference - maxPhaseDifference != 0;
    }

}
