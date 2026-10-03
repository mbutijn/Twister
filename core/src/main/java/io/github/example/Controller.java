package io.github.example;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;

public class Controller {
    private final Stage stage;
    private final Controllable rotationSpeedMainControllable, rotationSpeedSubControllable,
        pitchFrequencyControllable, pitchEquilibriumControllable, maxPhaseDifferenceControllable, pitchAmplitudeControllable;
    private final Array<Controllable> controllables;
    private float rotationSpeedMainValue, rotationSpeedSub, pitchFrequency, pitchEquilibrium, pitchAmplitude, maxPhaseDifference;
    private Status status;

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

        pitchFrequencyControllable = new Controllable(0.5f, 2f, 0.1f, 1f, sliderStyle, progressStyle);
        pitchFrequencyControllable.addNormalListener();

        pitchEquilibriumControllable = new Controllable(-0.2f, 0.6f, 0.01f, 0.2f, sliderStyle, progressStyle);
        addListenerWithDependencyForAmplitudeRange();

        pitchAmplitudeControllable = new Controllable(sliderStyle, progressStyle);
        pitchAmplitudeControllable.addNormalListener();

        maxPhaseDifferenceControllable = new Controllable(0, 4f * MathUtils.PI, 0.5f * MathUtils.PI, 0.5f * MathUtils.PI, sliderStyle, progressStyle);
        maxPhaseDifferenceControllable.addNormalListener();

        controllables = new Array<>();
        controllables.add(rotationSpeedMainControllable);
        controllables.add(rotationSpeedSubControllable);
        controllables.add(pitchFrequencyControllable);
        controllables.add(pitchEquilibriumControllable);
        controllables.add(pitchAmplitudeControllable);
        controllables.add(maxPhaseDifferenceControllable);

        table.left().bottom().pad(40);
        for (Controllable controllable : controllables) {
            controllable.addToTable(table);
        }

        table.row();
        Button changeStatusButton = getChangeStatusButton(sliderKnob);
        table.add(changeStatusButton).width(40).height(15).pad(2).colspan(2).center();

        Button dayNightButton = new Button(new Button.ButtonStyle(sliderKnob, sliderKnob, sliderKnob));
        dayNightButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Twister.switchDayNight();
            }
        });
        table.add(dayNightButton).width(40).height(15).pad(2).colspan(2).center();

        stage.addActor(table);

        rotationSpeedMainValue = rotationSpeedMainControllable.getTargetValue();
        rotationSpeedSub = rotationSpeedSubControllable.getTargetValue();
        pitchFrequency = pitchFrequencyControllable.getTargetValue();
        pitchEquilibrium = pitchEquilibriumControllable.getTargetValue();
        pitchAmplitude = pitchAmplitudeControllable.getTargetValue();
        maxPhaseDifference = maxPhaseDifferenceControllable.getTargetValue();

        status = Status.RUNNING;
    }

    private Button getChangeStatusButton(Drawable sliderKnob) {
        Button button = new Button(new Button.ButtonStyle(sliderKnob, sliderKnob, sliderKnob));
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (status == Status.RUNNING) {
                    status = Status.STOPPING;
                    setSlidersDisabled(true);
                    pitchFrequencyControllable.setToZero();
                    pitchEquilibriumControllable.setToMinimum();
                    pitchAmplitudeControllable.setToZero();
                    maxPhaseDifferenceControllable.setToZero();
                } else if (status == Status.PAUSED) {
                    status = Status.RUNNING;
                    setSlidersDisabled(false);
                    for (Controllable controllable : controllables) {
                        controllable.setToDefault();
                    }
                }
            }
        });
        return button;
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

    public void updateTimeDependentValues() {
        rotationSpeedMainValue = changeValue(rotationSpeedMainValue, rotationSpeedMainControllable.getTargetValue(), 0.005f);
        rotationSpeedSub = changeValue(rotationSpeedSub, rotationSpeedSubControllable.getTargetValue(), 0.008f);
        pitchEquilibrium = changeValue(pitchEquilibrium, pitchEquilibriumControllable.getTargetValue(), 0.004f);
        pitchAmplitude = changeValue(pitchAmplitude, pitchAmplitudeControllable.getTargetValue(), 0.005f);
        pitchFrequency = changeValue(pitchFrequency, pitchFrequencyControllable.getTargetValue(), 0.01f);
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

    public void handleStoppingStatus(float angleMain, float angleSub) {
        if (status == Status.STOPPING) {
            float stepMain = rotationSpeedMainControllable.getSlider().getStepSize();
            stepMain = rotationSpeedMainValue > 0 ? stepMain : -stepMain;
            boolean mainArmAlignedAndSlow = angleMain % (0.5f * MathUtils.PI) < 0.01f && Math.abs(rotationSpeedMainValue) <= Math.abs(stepMain);
            if (mainArmAlignedAndSlow) {
                setRotationSpeedMainTarget(0);
            } else {
                setRotationSpeedMainTarget(stepMain);
            }

            float stepSub = rotationSpeedSubControllable.getSlider().getStepSize();
            stepSub = rotationSpeedSub > 0 ? stepSub : -stepSub;
            boolean subArmAlignedAndSlow = Math.abs(angleSub + 0.25f * MathUtils.PI)  % (0.5f * MathUtils.PI) < 0.01f && Math.abs(rotationSpeedSub) <= Math.abs(stepSub);
            if (subArmAlignedAndSlow) {
                setRotationSpeedSubTarget(0);
            } else {
                setRotationSpeedSubTarget(stepSub);
            }

            if (mainArmAlignedAndSlow && subArmAlignedAndSlow) {
                status = Status.PAUSED;
            }
        }
    }

    public void setSlidersDisabled(boolean disabled) {
        for (Controllable controllable : controllables) {
            controllable.getSlider().setDisabled(disabled);
        }
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

    public void setRotationSpeedMainTarget(float rotationSpeedMain) {
        rotationSpeedMainControllable.setTargetValue(rotationSpeedMain);
        rotationSpeedMainControllable.getSlider().setValue(rotationSpeedMain);
    }

    public void setRotationSpeedSubTarget(float rotationSpeedSub) {
        rotationSpeedSubControllable.setTargetValue(rotationSpeedSub);
        rotationSpeedSubControllable.getSlider().setValue(rotationSpeedSub);
    }

    public float getRotationSpeedMain() {
        return rotationSpeedMainValue;
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

    public float getPitchFrequency() {
        return pitchFrequency;
    }

    public boolean isPhaseDifferenceNeedsChange() {
        return maxPhaseDifferenceControllable.getTargetValue() - maxPhaseDifference != 0;
    }

    public void drawTrueValues() {
        rotationSpeedMainControllable.updateProgressBarValue(rotationSpeedMainValue);
        rotationSpeedSubControllable.updateProgressBarValue(rotationSpeedSub);
        pitchEquilibriumControllable.updateProgressBarValue(pitchEquilibrium);
        pitchAmplitudeControllable.updateProgressBarValue(pitchAmplitude);
        maxPhaseDifferenceControllable.updateProgressBarValue(maxPhaseDifference);
        pitchFrequencyControllable.updateProgressBarValue(pitchFrequency);
    }

}
