package io.github.example;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class Controllable {
    private final Slider slider;
    private final ProgressBar progressBar;
    private float targetValue;

    public Controllable(float minValue, float maxValue, float step, float setValue, Slider.SliderStyle sliderStyle, Slider.SliderStyle progressStyle) {
        this.slider = new Slider(minValue, maxValue, step, true, sliderStyle);
        progressBar = new ProgressBar(minValue, maxValue, step, true, progressStyle);
        setProperties(setValue);
    }

    public Controllable(Slider.SliderStyle sliderStyle, Slider.SliderStyle progressStyle) {
        this.slider = new LimitedSlider(0, 0.4f, 0.01f, true, sliderStyle);
        progressBar = new ProgressBar(0, 0.4f, 0.01f, true, progressStyle);
        setProperties(0.4f);
    }

    public void setProperties(float setValue) {
        progressBar.setValue(setValue);
//        slider.setWidth(30);
//        slider.setHeight(300);
//        slider.setPosition(30, 250);
//        progressBar.setWidth(5);
//        progressBar.setHeight(300);
//        progressBar.setPosition(30, 250);
        slider.setValue(setValue);
        targetValue = setValue;
    }

    protected void addNormalListener() {
        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                targetValue = slider.getValue();
            }
        });
    }

    public void addToTable(Table table) {
        table.add(slider).width(10).pad(1);
        table.add(progressBar).width(5).pad(10);
    }

    public void updateProgressBarValue(float trueValue) {
        progressBar.setValue(trueValue);
    }

    public void setTargetValue(float targetValue) {
        this.targetValue = targetValue;
    }

    public float getTargetValue() {
        return targetValue;
    }

    public Slider getSlider() {
        return slider;
    }

    public LimitedSlider getLimitedSlider() {
        return (LimitedSlider) slider;
    }
}
