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
    private final Controllable rotationSpeedMainControllable, rotationSpeedSubControllable, pitchEquilibriumControllable, maxPhaseDifferenceControllable, pitchAmplitudeControllable;
    private float rotationSpeedMain, rotationSpeedSub, pitchEquilibrium, pitchAmplitude, maxPhaseDifference;

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

        pitchAmplitudeControllable = new Controllable(sliderStyle, progressStyle);
        pitchAmplitudeControllable.addNormalListener();

        maxPhaseDifferenceControllable = new Controllable(0, 4f * MathUtils.PI, 0.5f * MathUtils.PI, 0.5f * MathUtils.PI, sliderStyle, progressStyle);
        maxPhaseDifferenceControllable.addNormalListener();

        table.left().bottom().pad(50);
        rotationSpeedMainControllable.addToTable(table);
        rotationSpeedSubControllable.addToTable(table);
        pitchEquilibriumControllable.addToTable(table);
        pitchAmplitudeControllable.addToTable(table);
        maxPhaseDifferenceControllable.addToTable(table);

        stage.addActor(table);

        rotationSpeedMain = rotationSpeedMainControllable.getTargetValue();
        rotationSpeedSub = rotationSpeedSubControllable.getTargetValue();
        pitchEquilibrium = pitchEquilibriumControllable.getTargetValue();
        pitchAmplitude = pitchAmplitudeControllable.getTargetValue();
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

    private void updateAmplitudeRange() {
        Slider pitchEquilibriumSlider = pitchEquilibriumControllable.getSlider();
        float pitchEquilibrium = pitchEquilibriumSlider.getValue();
        float distanceToMin = pitchEquilibrium - pitchEquilibriumSlider.getMinValue();
        float distanceToMax = pitchEquilibriumSlider.getMaxValue() - pitchEquilibrium;
        pitchAmplitudeControllable.getLimitedSlider().setEffectiveMax(Math.min(distanceToMin, distanceToMax));
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
        pitchAmplitude = changeValue(pitchAmplitude, pitchAmplitudeControllable.getTargetValue(), 0.005f);
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
        pitchAmplitudeControllable.updateProgressBarValue(pitchAmplitude);
        maxPhaseDifferenceControllable.updateProgressBarValue(maxPhaseDifference);
    }

}
