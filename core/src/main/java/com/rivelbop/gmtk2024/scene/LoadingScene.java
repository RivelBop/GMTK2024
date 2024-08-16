package com.rivelbop.gmtk2024.scene;

import com.badlogic.gdx.graphics.Texture;
import com.rivelbop.rivelworks.util.Utils;

public class LoadingScene extends Scene {
    @Override
    public void init() {
        MAIN.assets.load("goat.png", Texture.class);
    }

    @Override
    public void render(float delta) {
        Utils.clearScreen2D();
        if (MAIN.assets.update()) { // Load and check if assets are loaded
            MAIN.setScreen(new MenuScene());
        }
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void dispose() {
    }
}
