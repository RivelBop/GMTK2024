package com.rivelbop.gmtk2024.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.rivelbop.rivelworks.util.Utils;

public class MenuScene extends Scene {
    private OrthographicCamera camera;
    private ScreenViewport viewport;
    private SpriteBatch spriteBatch;

    private Texture logo;

    private Skin skin;
    private Stage stage;
    private VerticalGroup verticalGroup;

    @Override
    public void init() {
        camera = new OrthographicCamera();
        viewport = new ScreenViewport(camera);
        camera.update();

        spriteBatch = new SpriteBatch();
        logo = new Texture("rivelbop.png");
        skin = new Skin(Gdx.files.internal("skin/comic-ui.json"));
        stage = new Stage(viewport, spriteBatch);

        verticalGroup = new VerticalGroup();
        verticalGroup.space(50f);
        verticalGroup.center();
        verticalGroup.addActor(
            new TextButton("Play", skin) {{
                this.addListener(event -> {
                    if (event.isHandled()) {
                        MAIN.setScreen(new GameScene());
                        return true;
                    }
                    return false;
                });
            }}
        );
        verticalGroup.addActor(
            new TextButton("Quit", skin) {{
                this.addListener(event -> {
                    if (event.isHandled()) {
                        Gdx.app.exit();
                        return true;
                    }
                    return false;
                });
            }}
        );

        stage.addActor(verticalGroup);
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Utils.clearScreen2D();

        stage.act(delta);
        camera.update();
        viewport.apply(true);

        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        spriteBatch.draw(logo, 10f, 10f, 64f, 64f);
        spriteBatch.end();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        verticalGroup.setPosition(width / 2f, height / 2f, Align.center);
    }

    @Override
    public void dispose() {
        logo.dispose();
        skin.dispose();
        stage.dispose();
        spriteBatch.dispose();
        Gdx.input.setInputProcessor(null);
    }
}
