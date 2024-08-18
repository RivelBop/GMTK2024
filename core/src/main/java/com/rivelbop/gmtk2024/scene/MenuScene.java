package com.rivelbop.gmtk2024.scene;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
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
    private SpriteBatch batch;

    private Skin skin;
    private Stage stage;
    private VerticalGroup verticalGroup;

    @Override
    public void init() {
        camera = new OrthographicCamera();
        viewport = new ScreenViewport(camera);
        camera.update();

        batch = new SpriteBatch();
        skin = new Skin(Gdx.files.internal("skin/comic-ui.json"));
        stage = new Stage(viewport, batch);

        verticalGroup = new VerticalGroup();
        verticalGroup.space(10f);
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

        stage.draw();
        MAIN.setScreen(new GameScene());
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        verticalGroup.setPosition(width / 2f, height / 2f, Align.center);
    }

    @Override
    public void dispose() {
        batch.dispose();
        skin.dispose();
        stage.dispose();
    }
}
