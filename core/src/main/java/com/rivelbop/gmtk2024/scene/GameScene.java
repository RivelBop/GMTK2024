package com.rivelbop.gmtk2024.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.rivelbop.gmtk2024.Main;
import com.rivelbop.gmtk2024.Physics;
import com.rivelbop.gmtk2024.block.Blocks;
import com.rivelbop.gmtk2024.block.Spike;
import com.rivelbop.gmtk2024.block.Wind;
import com.rivelbop.gmtk2024.entity.Enemies;
import com.rivelbop.gmtk2024.entity.Player;
import com.rivelbop.rivelworks.g2d.graphics.ShapeBatch;
import com.rivelbop.rivelworks.g2d.map.OrthogonalMap;
import com.rivelbop.rivelworks.ui.Font;
import com.rivelbop.rivelworks.util.Utils;
import de.pottgames.tuningfork.SoundBuffer;
import de.pottgames.tuningfork.SoundLoader;
import de.pottgames.tuningfork.StreamedSoundSource;

public class GameScene extends Scene {
    private static final float BACKGROUND_LERP = 1 / 20f;

    private enum Background {
        GRASS,
        SKY,
        SPACE
    }

    private Array<Rectangle> grass, sky, space;
    private Sprite bg_grass, bg_sky, bg_space;
    private Background currentBackground = Background.GRASS;

    private OrthographicCamera camera;
    private ExtendViewport viewport;
    private SpriteBatch spriteBatch;
    private Font font;

    private OrthogonalMap map;
    private World physicsWorld;
    private Array<Body> physicsBodies;
    private Array<Wind> winds;
    private Player player;

    private final Array<SoundBuffer> sounds = new Array<>();
    private boolean hasFallen;
    private float timer;

    private Rectangle win;
    private float winDuration, winTimer;
    private boolean hasWon;

    private StreamedSoundSource music, ambient;

    @Override
    public void init() {
        music = new StreamedSoundSource(Gdx.files.internal("music.ogg"));
        music.setVolume(0.4f);
        music.setLooping(true);
        music.play();
        ambient = new StreamedSoundSource(Gdx.files.internal("mixkit-morning-birds-2472.ogg"));
        ambient.setVolume(0.1f);
        ambient.setLooping(true);
        ambient.play();
        camera = new OrthographicCamera();
        viewport = new ExtendViewport(Main.WIDTH, Main.HEIGHT, camera);
        camera.zoom = 1.5f;
        camera.update();
        spriteBatch = new SpriteBatch();

        bg_grass = new Sprite(MAIN.assets.get("bg_grass.png", Texture.class));
        bg_sky = new Sprite(MAIN.assets.get("bg_sky.png", Texture.class));
        bg_space = new Sprite(MAIN.assets.get("bg_space.png", Texture.class));

        map = new OrthogonalMap("map.tmx");
        physicsWorld = new World(new Vector2(0f, -2.5f), true);
        map.boundingShapesToPhysicsWorld("collision", physicsWorld, Physics.PPM);
        for (Rectangle r : map.getBoundingShapes(Rectangle.class, "goats")) {
            Enemies.GOAT.create(physicsWorld, r.x, r.y);
        }

        Array<Rectangle> windRect = map.getBoundingShapes(Rectangle.class, "winds");
        winds = new Array<>(windRect.size);
        for (Rectangle r : windRect) {
            winds.add(new Wind(r.x, r.y, r.width, r.height, new Vector2(3f, 0f)));
        }

        for (Rectangle r : map.getBoundingShapes(Rectangle.class, "crate")) {
            Blocks.CRATE.create(physicsWorld, new Vector2(r.x, r.y));
        }

        grass = map.getBoundingShapes(Rectangle.class, "grass");
        sky = map.getBoundingShapes(Rectangle.class, "sky");
        space = map.getBoundingShapes(Rectangle.class, "space");

        player = new Player(MAIN.assets, physicsWorld);
        physicsWorld.setContactListener(new Physics.PhysicsListener(physicsWorld, player));
        physicsBodies = new Array<>();

        for (Rectangle r : map.getBoundingShapes(Rectangle.class, "spikes")) {
            new Spike(physicsWorld, MAIN.assets, new Vector2(r.x, r.y), player);
        }

        sounds.add(SoundLoader.load(Gdx.files.internal("start.wav")));
        for (int i = 0; i < 19; i++) {
            sounds.add(SoundLoader.load(Gdx.files.internal("audio_" + i + ".ogg")));
        }
        sounds.add(SoundLoader.load(Gdx.files.internal("win.ogg")));
        sounds.get(0).play();

        Font.FontBuilder builder = new Font.FontBuilder();
        font = builder.
            setFont(Gdx.files.internal("font.ttf")).
            setSize(64).
            build();
        builder.dispose();

        win = map.getBoundingShapes(Rectangle.class, "david").get(0);
    }

    @Override
    public void render(float delta) {
        Utils.clearScreen2D();

        player.update();
        for (Wind w : winds) {
            player.applyWind(w);
        }

        if (player.sprite.getBoundingRectangle().overlaps(win)) {
            if (!hasWon) {
                winDuration = sounds.get(sounds.size - 1).getDuration();
                sounds.get(sounds.size - 1).play();
                hasWon = true;
            }
        }

        if (hasWon) {
            winTimer += delta;
            if (winTimer >= winDuration) {
                Enemies.GOAT.create(physicsWorld, player.sprite.getX(), player.sprite.getY());
            }
        }

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
        Sprite sprite = player.sprite;
        camera.position.set(sprite.getX() + sprite.getWidth() / 2f, sprite.getY() + sprite.getHeight() / 2f, 0f);
        camera.update();
        viewport.apply();

        for (Rectangle r : grass) {
            if (player.sprite.getBoundingRectangle().overlaps(r)) {
                currentBackground = Background.GRASS;
                break;
            }
        }
        for (Rectangle r : sky) {
            if (player.sprite.getBoundingRectangle().overlaps(r)) {
                currentBackground = Background.SKY;
                break;
            }
        }
        for (Rectangle r : space) {
            if (player.sprite.getBoundingRectangle().overlaps(r)) {
                currentBackground = Background.SPACE;
                break;
            }
        }

        if (currentBackground == Background.GRASS) {
            bg_grass.setAlpha(MathUtils.lerp(bg_grass.getColor().a, 1f, BACKGROUND_LERP));
            bg_sky.setAlpha(MathUtils.lerp(bg_sky.getColor().a, 0f, BACKGROUND_LERP));
            bg_space.setAlpha(MathUtils.lerp(bg_space.getColor().a, 0f, BACKGROUND_LERP));
        } else if (currentBackground == Background.SKY) {
            bg_grass.setAlpha(MathUtils.lerp(bg_grass.getColor().a, 0f, BACKGROUND_LERP));
            bg_sky.setAlpha(MathUtils.lerp(bg_sky.getColor().a, 1f, BACKGROUND_LERP));
            bg_space.setAlpha(MathUtils.lerp(bg_space.getColor().a, 0f, BACKGROUND_LERP));
        } else {
            bg_grass.setAlpha(MathUtils.lerp(bg_grass.getColor().a, 0f, BACKGROUND_LERP));
            bg_sky.setAlpha(MathUtils.lerp(bg_sky.getColor().a, 0f, BACKGROUND_LERP));
            bg_space.setAlpha(MathUtils.lerp(bg_space.getColor().a, 1f, BACKGROUND_LERP));
        }

        Vector3 pos = camera.unproject(new Vector3(0f, viewport.getScreenHeight(), 0f));
        bg_grass.setPosition(pos.x, pos.y);
        bg_sky.setPosition(pos.x, pos.y);
        bg_space.setPosition(pos.x, pos.y);

        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();

        bg_grass.draw(spriteBatch);
        bg_sky.draw(spriteBatch);
        bg_space.draw(spriteBatch);

        for (Body b : physicsBodies) {
            if (b.getUserData() instanceof Physics.BodyData) {
                Physics.BodyData bodyData = (Physics.BodyData) b.getUserData();
                bodyData.render(spriteBatch);
            }
        }
        font.getBitmapFont().setColor(Color.WHITE);
        font.drawCenter(spriteBatch, "A/D - Move Horizontally\nW - Launch Crates\nSpace - Jump\nR - Restart", 400f, 500f);
        spriteBatch.end();

        map.render(camera);

        if (!hasWon) {
            timer += delta;
        }
        spriteBatch.setProjectionMatrix(camera.projection);
        spriteBatch.begin();
        font.getBitmapFont().setColor(Color.YELLOW);
        font.drawCenter(spriteBatch, String.valueOf((int) timer), 0f, Main.HEIGHT / 2f + 50f);
        spriteBatch.end();

        camera.combined.scl(Physics.PPM);

        if (player.physicsBody.getLinearVelocity().y > -5f) {
            hasFallen = false;
        }

        if (!hasFallen && player.physicsBody.getLinearVelocity().y < -5f) {
            hasFallen = true;
            int random = MathUtils.random(1, 19);
            sounds.get(random).play();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }

        if (player.isKilled || Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            MAIN.setScreen(new GameScene());
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void dispose() {
        for (SoundBuffer s : sounds) {
            s.dispose();
        }
        player.jumpSound.dispose();
        ambient.dispose();
        music.dispose();

        font.dispose();
        spriteBatch.dispose();

        physicsWorld.dispose();
    }
}
