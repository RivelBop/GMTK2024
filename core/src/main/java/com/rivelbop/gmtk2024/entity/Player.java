package com.rivelbop.gmtk2024.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.rivelbop.gmtk2024.Main;
import com.rivelbop.gmtk2024.Physics;
import com.rivelbop.gmtk2024.block.Wind;
import com.rivelbop.rivelworks.g2d.physics.body.DynamicBody2D;
import com.rivelbop.rivelworks.io.Assets;
import de.pottgames.tuningfork.SoundBuffer;
import de.pottgames.tuningfork.SoundLoader;

public class Player {
    private final float MAX_VELOCITY_X = 3f, RADIUS = 50f / Physics.PPM;
    private final World PHYSICS_WORLD; // No need to store
    public Body physicsBody, hittingBody, standingBody;
    public Sprite sprite;
    protected boolean onGround;
    public boolean isFlung, isKilled;
    public SoundBuffer jumpSound;
    private ParticleEffect walkPar, jumpPar;

    private final RayCastCallback RAY_CALLBACK =
        (fixture, point, normal, fraction) -> {
            standingBody = fixture.getBody();
            onGround = true;
            return 0;
        };

    public Player(Assets assets, World world) {
        sprite = new Sprite(assets.get("goat.png", Texture.class));
        sprite.setSize(sprite.getWidth() * 0.2f, sprite.getHeight() * 0.2f);

        jumpSound = SoundLoader.load(Gdx.files.internal("mixkit-arrow-whoosh-1491_1.ogg"));

        //walkPar = new ParticleE
        //jumpPar = new ParticleEmitter();

        // Physics body
        CircleShape shape = new CircleShape();
        shape.setRadius(RADIUS);
        physicsBody = new DynamicBody2D(world, shape, 1f, 10f, 0f).getBody();
        physicsBody.setSleepingAllowed(false);
        physicsBody.setTransform(Main.WIDTH / 2f / Physics.PPM, Main.HEIGHT / 2f / Physics.PPM, 0f);
        physicsBody.setUserData(new Physics.BodyData(physicsBody, sprite));

        PHYSICS_WORLD = world;
    }

    public void update() {
        updateMovement();
        moveBody();
    }

    private void updateMovement() {
        if (isFlung && onGround) {
            isFlung = false;
        }

        Vector2 velocity = physicsBody.getLinearVelocity();
        if (!isFlung) {
            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                physicsBody.applyForceToCenter(-3f, 0f, true);
            }

            if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                physicsBody.applyForceToCenter(3f, 0f, true);
            }

            // Clamp the velocity of the player
            velocity.x = MathUtils.clamp(velocity.x, -MAX_VELOCITY_X, MAX_VELOCITY_X);
        }

        onGround = false;
        PHYSICS_WORLD.rayCast(RAY_CALLBACK, physicsBody.getPosition().cpy().sub(0f, RADIUS), physicsBody.getPosition().cpy().sub(0f, RADIUS + 0.01f));
        if (onGround && Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            jumpSound.play(0.6f);
            velocity.y = 3f; // 3
        }
        physicsBody.setLinearVelocity(velocity);
    }

    private void moveBody() {
        if (hittingBody != null) {
            Vector2 velocity = hittingBody.getLinearVelocity();
            if (onGround && standingBody != hittingBody && Gdx.input.isKeyPressed(Input.Keys.W)) {
                boolean left = Gdx.input.isKeyPressed(Input.Keys.A);
                boolean right = Gdx.input.isKeyPressed(Input.Keys.D);

                velocity.x = ((left && right) || (!left && !right)) ? 0f : (left) ? -3f : 3f;
                velocity.y = 3f;
            }
            hittingBody.setLinearVelocity(velocity);
        }
    }

    public void applyWind(Wind wind) {
        if (sprite.getBoundingRectangle().overlaps(wind)) {
            physicsBody.applyForceToCenter(wind.DIRECTION_STRENGTH, true);
        }
    }
}
