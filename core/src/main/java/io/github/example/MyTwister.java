package io.github.example;

import com.badlogic.gdx.Game;

public class MyTwister extends Game {
    @Override
    public void create() {
        setScreen(new Twister());
    }
}
