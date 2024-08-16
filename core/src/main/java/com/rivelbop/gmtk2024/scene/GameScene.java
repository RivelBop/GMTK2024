package com.rivelbop.gmtk2024.scene;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.rivelbop.gmtk2024.Main;
import com.rivelbop.gmtk2024.entity.Player;
import com.rivelbop.rivelworks.g2d.physics.body.StaticBody2D;
import com.rivelbop.rivelworks.util.Utils;

public class GameScene extends Scene {
    private OrthographicCamera camera;
    private ExtendViewport viewport;
    private SpriteBatch spriteBatch;

    private World physicsWorld;
    private Player player;

    // DEBUGGING
    private Box2DDebugRenderer physicsRenderer;

    @Override
    public void init() {
        camera = new OrthographicCamera();
        viewport = new ExtendViewport(Main.WIDTH, Main.HEIGHT, camera);
        camera.update();
        spriteBatch = new SpriteBatch();
        physicsRenderer = new Box2DDebugRenderer();

        physicsWorld = new World(new Vector2(0f, -20f / Main.PPM), true);
        new StaticBody2D(physicsWorld, new PolygonShape() {{
            this.setAsBox(Main.WIDTH / 2f / Main.PPM, Main.HEIGHT / 4f / Main.PPM);
        }});
        player = new Player(MAIN.assets, physicsWorld);
    }

    @Override
    public void render(float delta) {
        Utils.clearScreen2D();
        player.update();

        // Update physics
        physicsWorld.step(delta, 8, 3);
        player.updateSprite();

        // Rendering
        camera.update();
        viewport.apply(true);

        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        player.sprite.draw(spriteBatch);
        spriteBatch.end();

        camera.combined.scl(Main.PPM);
        physicsRenderer.render(physicsWorld, camera.combined);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
        physicsRenderer.dispose();
        physicsWorld.dispose();
    }
}
