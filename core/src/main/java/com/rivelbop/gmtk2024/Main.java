package com.rivelbop.gmtk2024;

import com.badlogic.gdx.Game;
import com.esotericsoftware.minlog.Log;
import com.rivelbop.gmtk2024.scene.LoadingScene;
import com.rivelbop.rivelworks.RivelWorks;
import com.rivelbop.rivelworks.io.Assets;

public class Main extends Game {
    public static final int HEIGHT = 480, WIDTH = HEIGHT * 16 / 9;
    public static final float PPM = 100f;
    public Assets assets;

    @Override
    public void create() {
        // TODO: Change logging upon release
        RivelWorks.init(Log.LEVEL_DEBUG, null, true, false);
        assets = new Assets();
        this.setScreen(new LoadingScene());
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        this.setScreen(null); // Hides and disposes the current screen
        assets.dispose();
        RivelWorks.dispose();
    }
}
