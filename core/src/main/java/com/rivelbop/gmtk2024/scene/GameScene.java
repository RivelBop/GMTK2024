package com.rivelbop.gmtk2024.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.rivelbop.gmtk2024.Main;
import com.rivelbop.gmtk2024.Physics;
import com.rivelbop.gmtk2024.block.Blocks;
import com.rivelbop.gmtk2024.block.Wind;
import com.rivelbop.gmtk2024.entity.Enemies;
import com.rivelbop.gmtk2024.entity.Player;
import com.rivelbop.rivelworks.g2d.graphics.ShapeBatch;
import com.rivelbop.rivelworks.g2d.physics.body.StaticBody2D;
import com.rivelbop.rivelworks.util.Utils;

public class GameScene extends Scene {
    private OrthographicCamera camera;
    private ExtendViewport viewport;
    private SpriteBatch spriteBatch;

    private World physicsWorld;
    private Array<Body> physicsBodies;
    private Player player;
    private Wind wind;

    // DEBUGGING
    private Box2DDebugRenderer physicsRenderer;
    private ShapeBatch shapeBatch;

    @Override
    public void init() {
        camera = new OrthographicCamera();
        viewport = new ExtendViewport(Main.WIDTH, Main.HEIGHT, camera);
        camera.update();
        spriteBatch = new SpriteBatch();
        physicsRenderer = new Box2DDebugRenderer();
        shapeBatch = new ShapeBatch();

        physicsWorld = new World(new Vector2(0f, -2f), true);
        player = new Player(MAIN.assets, physicsWorld);
        physicsWorld.setContactListener(new Physics.PhysicsListener(physicsWorld, player));
        physicsBodies = new Array<>();

        // DEBUG
        new StaticBody2D(physicsWorld, new PolygonShape() {{
            this.setAsBox(Main.WIDTH / 2f / Physics.PPM, Main.HEIGHT / 4f / Physics.PPM);
        }});

        Blocks.CRATE.create(physicsWorld, 150f, 150f);
        Enemies.GOAT.create(physicsWorld, 250f, 150f);
        wind = new Wind(150f, 150f, 150f, 50f, new Vector2(-1.1f, 0f));
    }

    @Override
    public void render(float delta) {
        Utils.clearScreen2D();

        player.update();
        player.applyWind(wind);

        // Update physics
        physicsWorld.step(delta, 8, 3);
        physicsWorld.getBodies(physicsBodies);
        for (Body b : physicsBodies) {
            if (b.getUserData() instanceof Physics.BodyData) {
                Physics.BodyData bodyData = (Physics.BodyData) b.getUserData();
                bodyData.update();
            }
        }

        // Rendering
        camera.update();
        viewport.apply(true);

        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        for (Body b : physicsBodies) {
            if (b.getUserData() instanceof Physics.BodyData) {
                Physics.BodyData bodyData = (Physics.BodyData) b.getUserData();
                bodyData.render(spriteBatch);
            }
        }
        spriteBatch.end();

        shapeBatch.setProjectionMatrix(camera.combined);
        shapeBatch.begin(ShapeRenderer.ShapeType.Line);
        shapeBatch.rect(wind);
        shapeBatch.rect(player.sprite.getBoundingRectangle());
        shapeBatch.end();

        camera.combined.scl(Physics.PPM);
        physicsRenderer.render(physicsWorld, camera.combined);

        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            MAIN.setScreen(new GameScene());
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
        physicsRenderer.dispose();
        physicsWorld.dispose();
    }
}
