package com.rivelbop.gmtk2024.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.rivelbop.gmtk2024.Main;
import com.rivelbop.gmtk2024.Physics;
import com.rivelbop.gmtk2024.entity.Player;
import com.rivelbop.rivelworks.g2d.map.OrthogonalMap;
import com.rivelbop.rivelworks.ui.Font;
import com.rivelbop.rivelworks.util.Utils;

public class MenuScene extends Scene {
    private OrthographicCamera camera;
    private ScreenViewport viewport;
    private StretchViewport stretchViewport;
    private SpriteBatch spriteBatch;

    private Sprite logo;

    private Skin skin;
    private Stage stage;
    private VerticalGroup verticalGroup;

    private OrthogonalMap map;
    private World physicsWorld;
    private final Array<Body> physicsBodies = new Array<>();

    private Array<Rectangle> spawn;

    private Font font;
    private float timer;

    @Override
    public void init() {
        camera = new OrthographicCamera();
        viewport = new ScreenViewport(camera);
        stretchViewport = new StretchViewport(Main.WIDTH, Main.HEIGHT, camera);
        camera.update();

        spriteBatch = new SpriteBatch();
        logo = new Sprite(new Texture("rivelbop.png"));
        logo.setSize(64f, 64f);
        logo.setPosition(10f, 10f);
        skin = new Skin(Gdx.files.internal("skin/comic-ui.json"));
        stage = new Stage(viewport, spriteBatch);

        Font.FontBuilder builder = new Font.FontBuilder();
        font = builder.
            setFont(Gdx.files.internal("font.ttf")).
            setSize(128).
            build();
        builder.dispose();

        verticalGroup = new VerticalGroup();
        verticalGroup.space(25f);
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

        verticalGroup.setScale(1.25f);
        stage.addActor(verticalGroup);
        Gdx.input.setInputProcessor(stage);

        map = new OrthogonalMap("menuMap.tmx");
        physicsWorld = new World(new Vector2(0f, -2f), true);
        map.boundingShapesToPhysicsWorld("collision", physicsWorld, Physics.PPM);
        spawn = map.getBoundingShapes(Rectangle.class, "spawn");
    }

    @Override
    public void render(float delta) {
        Utils.clearScreen2D();

        if (physicsWorld.getBodyCount() < 31f) {
            timer += delta;
            if (timer >= 0.33f) {
                timer = 0f;
                for (Rectangle r : spawn) {
                    new Player(MAIN.assets, physicsWorld, new Vector2(r.x, r.y)).physicsBody.applyForceToCenter(MathUtils.random(-20f, 20f), 0f, true);
                }
                physicsWorld.getBodies(physicsBodies);
            }
        }

        physicsWorld.step(delta, 8, 3);
        stage.act(delta);
        camera.update();

        stretchViewport.apply(true);
        map.render(camera);
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        for (Body b : physicsBodies) {
            if (b.getUserData() instanceof Physics.BodyData) {
                Physics.BodyData bodyData = (Physics.BodyData) b.getUserData();
                bodyData.update();
                bodyData.render(spriteBatch);
            }
        }
        spriteBatch.end();

        viewport.apply();
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        font.drawCenter(spriteBatch, "GoatScale", Main.WIDTH / 2f, Main.HEIGHT / 2f + 100f);
        spriteBatch.end();

        viewport.apply(true);
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        logo.draw(spriteBatch);
        spriteBatch.end();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        stretchViewport.update(width, height);
        verticalGroup.setPosition(width / 2f, height / 3f, Align.center);
    }

    @Override
    public void dispose() {
        map.dispose();
        physicsWorld.dispose();

        logo.getTexture().dispose();
        font.dispose();
        skin.dispose();
        stage.dispose();

        spriteBatch.dispose();
        Gdx.input.setInputProcessor(null);
    }
}
