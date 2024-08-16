package com.rivelbop.gmtk2024.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.esotericsoftware.minlog.Log;
import com.rivelbop.gmtk2024.Main;

public abstract class Scene implements Screen {
    /**
     * Used to change screens.
     */
    protected final Main MAIN = (Main) Gdx.app.getApplicationListener();

    public abstract void init();

    @Override
    public void show() {
        Log.debug(this.getClass().getSimpleName(), "Entered scene.");
        init();
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
        dispose();
        Log.debug(this.getClass().getSimpleName(), "Exited scene.");
    }
}
