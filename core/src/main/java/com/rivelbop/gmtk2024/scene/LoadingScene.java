package com.rivelbop.gmtk2024.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.rivelbop.gmtk2024.block.Blocks;
import com.rivelbop.gmtk2024.entity.Enemies;
import com.rivelbop.rivelworks.g2d.graphics.ShapeBatch;
import com.rivelbop.rivelworks.ui.Font;
import com.rivelbop.rivelworks.util.Utils;

public class LoadingScene extends Scene {
    private static final float HEIGHT = 50f;

    private OrthographicCamera camera;
    private ScreenViewport viewport;
    private SpriteBatch spriteBatch;
    private ShapeBatch shapeBatch;

    private Font font;
    private Rectangle backgroundBar, bar;

    @Override
    public void init() {
        camera = new OrthographicCamera();
        viewport = new ScreenViewport(camera);
        spriteBatch = new SpriteBatch();
        shapeBatch = new ShapeBatch();

        Font.FontBuilder builder = new Font.FontBuilder();
        font = builder.
            setFont(Gdx.files.internal("font.ttf")).
            setSize(64).
            build();
        builder.dispose();

        backgroundBar = new Rectangle(0f, 0f, 0f, HEIGHT);
        bar = new Rectangle(0f, 0f, 0f, HEIGHT);

        Blocks.loadTexturesToAssets(MAIN.assets);
        Enemies.loadTexturesToAssets(MAIN.assets);
    }

    @Override
    public void render(float delta) {
        Utils.clearScreen2D();

        bar.setWidth(viewport.getScreenWidth() * MAIN.assets.getAssetManager().getProgress());

        camera.update();
        viewport.apply(true);
        spriteBatch.setProjectionMatrix(camera.combined);
        shapeBatch.setProjectionMatrix(camera.combined);

        spriteBatch.begin();
        font.drawCenter(spriteBatch, "LOADING",
            viewport.getScreenWidth() / 2f, viewport.getScreenHeight() / 2f);
        spriteBatch.end();

        shapeBatch.begin(ShapeRenderer.ShapeType.Filled);
        shapeBatch.setColor(Color.GRAY);
        shapeBatch.rect(backgroundBar);
        shapeBatch.setColor(Color.RED);
        shapeBatch.rect(bar);
        shapeBatch.end();

        if (MAIN.assets.update()) { // Load and check if assets are loaded
            Blocks.loadAssetsToTextures(MAIN.assets);
            Enemies.loadAssetsToTextures(MAIN.assets);
            MAIN.setScreen(new MenuScene());
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        float y = height / 3f - HEIGHT;
        backgroundBar.setWidth(width);
        backgroundBar.setY(y);
        bar.setY(y);
    }

    @Override
    public void dispose() {
        font.dispose();
        shapeBatch.dispose();
    }
}
