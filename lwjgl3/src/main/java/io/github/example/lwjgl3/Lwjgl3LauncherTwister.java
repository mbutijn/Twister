package io.github.example.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import io.github.example.MyTwister;

public class Lwjgl3LauncherTwister {
    public static void main(String[] args) {
        if (StartupHelper.startNewJvmIfRequired()) return; // This handles macOS support and helps on Windows.

        createTwisterApplication();
    }

    private static void createTwisterApplication() {
        Lwjgl3ApplicationConfiguration config =
            new Lwjgl3ApplicationConfiguration();

        config.setTitle("Twister");
        config.setWindowedMode(1280, 720);

        new Lwjgl3Application(new MyTwister(), config);
    }
}
