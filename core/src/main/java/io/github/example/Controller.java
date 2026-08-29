package io.github.example;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class Controller {
    private final Stage stage;
    private final LimitedSlider pitchAmplitudeSlider;
    private final ProgressBar pitchAmplitudeProgressBar;
    private float rotationSpeedMain, rotationSpeedSub, pitchEquilibrium, pitchAmplitude, maxPhaseDifference;
    private float targetPitchAmplitude;
    private final Controllable rotationSpeedMainControllable, rotationSpeedSubControllable, pitchEquilibriumControllable, maxPhaseDifferenceControllable; // pitchAmplitudeControllable,

    public Controller() {
        stage = new Stage();
        Table table = new Table();
        table.setFillParent(true);
        table.row();

        Drawable sliderBackground = new TextureRegionDrawable(
            new TextureRegion(new Texture("ui/slider-background.png"))
        );
        Drawable sliderKnob = new TextureRegionDrawable(
            new TextureRegion(new Texture("ui/slider-knob.png"))
        );

        Drawable progressBackground = new TextureRegionDrawable(
            new TextureRegion(new Texture("ui/progress-background.png"))
        );
        Drawable progressKnob = new TextureRegionDrawable(
            new TextureRegion(new Texture("ui/progress-knob.png"))
        );

        Slider.SliderStyle sliderStyle = new Slider.SliderStyle();
        sliderStyle.background = sliderBackground;
        sliderStyle.knob = sliderKnob;

        Slider.SliderStyle progressStyle = new Slider.SliderStyle();
        progressStyle.background = progressBackground;
        progressStyle.knob = progressKnob;

        // Add the ui elements
        rotationSpeedMainControllable = new Controllable(-1.2f, 1.2f, 0.05f, 0.75f, sliderStyle, progressStyle);
        rotationSpeedMainControllable.addNormalListener();

        rotationSpeedSubControllable = new Controllable(-2f, 2f, 0.1f, 1.2f, sliderStyle, progressStyle);
        rotationSpeedSubControllable.addNormalListener();

        pitchEquilibriumControllable = new Controllable(-0.2f, 0.6f, 0.01f, 0.2f, sliderStyle, progressStyle);
        addListenerWithDependencyForAmplitudeRange();

        pitchAmplitudeSlider = new LimitedSlider(0, 0.4f, 0.01f, true, sliderStyle);
        pitchAmplitudeProgressBar = new ProgressBar(0, 0.4f, 0.01f, true, progressStyle);
        addDimensionsToSlider(pitchAmplitudeSlider);
        pitchAmplitudeSlider.setValue(0.4f);
        addDimensionsToProgressBar(pitchAmplitudeProgressBar);
        targetPitchAmplitude = 0.4f;
        pitchAmplitudeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                targetPitchAmplitude = pitchAmplitudeSlider.getValue();
            }
        });

        maxPhaseDifferenceControllable = new Controllable(0, 4f * MathUtils.PI, 0.5f * MathUtils.PI, 0.5f * MathUtils.PI, sliderStyle, progressStyle);
        maxPhaseDifferenceControllable.addNormalListener();

        table.left().bottom().pad(20);
        rotationSpeedMainControllable.addToTable(table);
        rotationSpeedSubControllable.addToTable(table);
        pitchEquilibriumControllable.addToTable(table);
        table.add(pitchAmplitudeSlider).width(10).pad(15);
        table.add(pitchAmplitudeProgressBar).width(10).pad(15);
        maxPhaseDifferenceControllable.addToTable(table);

        stage.addActor(table);

        rotationSpeedMain = rotationSpeedMainControllable.getTargetValue();
        rotationSpeedSub = rotationSpeedSubControllable.getTargetValue();
        pitchEquilibrium = pitchEquilibriumControllable.getTargetValue();
        pitchAmplitude = targetPitchAmplitude;
        maxPhaseDifference = maxPhaseDifferenceControllable.getTargetValue();
    }

    private void addListenerWithDependencyForAmplitudeRange() {
        Slider slider = pitchEquilibriumControllable.getSlider();
        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                pitchEquilibriumControllable.setTargetValue(slider.getValue());
                updateAmplitudeRange();
            }
        });
    }

    private void addDimensionsToSlider(Slider slider) {
        slider.setWidth(30);
        slider.setHeight(300);
        slider.setPosition(30, 250);
    }

    private void addDimensionsToProgressBar(ProgressBar progressBar) {
        progressBar.setWidth(10);
        progressBar.setHeight(300);
        progressBar.setPosition(30, 250);
    }

    private void updateAmplitudeRange() {
        Slider pitchEquilibriumSlider = pitchEquilibriumControllable.getSlider();
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
        rotationSpeedMain = changeValue(rotationSpeedMain, rotationSpeedMainControllable.getTargetValue(), 0.005f);
        rotationSpeedSub = changeValue(rotationSpeedSub, rotationSpeedSubControllable.getTargetValue(), 0.008f);
        pitchEquilibrium = changeValue(pitchEquilibrium, pitchEquilibriumControllable.getTargetValue(), 0.004f);
        pitchAmplitude = changeValue(pitchAmplitude, targetPitchAmplitude, 0.005f);
        maxPhaseDifference = changeValue(maxPhaseDifference, maxPhaseDifferenceControllable.getTargetValue(), 0.01f);
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
        return maxPhaseDifferenceControllable.getTargetValue() - maxPhaseDifference != 0;
    }

    public void drawTrueValues() {
        rotationSpeedMainControllable.updateProgressBarValue(rotationSpeedMain);
        rotationSpeedSubControllable.updateProgressBarValue(rotationSpeedSub);
        pitchEquilibriumControllable.updateProgressBarValue(pitchEquilibrium);
        pitchAmplitudeProgressBar.setValue(pitchAmplitude);
        maxPhaseDifferenceControllable.updateProgressBarValue(maxPhaseDifference);
    }

}
