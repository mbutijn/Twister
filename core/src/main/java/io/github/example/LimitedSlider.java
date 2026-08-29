package io.github.example;

import com.badlogic.gdx.scenes.scene2d.ui.Slider;

public class LimitedSlider extends Slider {
    private float effectiveMax;

    public LimitedSlider(float min, float max, float stepSize, boolean vertical, SliderStyle style) {
        super(min, max, stepSize, vertical, style);
        effectiveMax = max;
    }

    public void setEffectiveMax(float effectiveMax) {
        this.effectiveMax = Math.min(effectiveMax, this.getMaxValue());
        if (getValue() > this.effectiveMax) {
            super.setValue(this.effectiveMax);
        }
    }

    public float getEffectiveMax() {
        return effectiveMax;
    }

    @Override
    public boolean setValue(float value) {
        super.setValue(Math.min(value, effectiveMax));
        return false;
    }
}

