package com.rivelbop.gmtk2024.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.rivelbop.gmtk2024.Main;
import com.rivelbop.rivelworks.g2d.physics.body.DynamicBody2D;
import com.rivelbop.rivelworks.io.Assets;

public class Player {
    private final float MAX_VELOCITY_X = 2f, RADIUS = 25f / Main.PPM;
    private final World PHYSICS_WORLD;
    protected boolean canJump;

    private final RayCastCallback RAY_CALLBACK =
        (fixture, point, normal, fraction) -> {
            canJump = true;
            return 0;
        };

    public Sprite sprite;
    public Body physicsBody;

    public Player(Assets assets, World world) {
        sprite = new Sprite(assets.get("goat.png", Texture.class));
        sprite.setSize(sprite.getWidth() * 0.1f, sprite.getHeight() * 0.1f);

        // Physics body
        CircleShape shape = new CircleShape();
        shape.setRadius(RADIUS);
        physicsBody = new DynamicBody2D(world, shape, 1f, 1f, 1f).getBody();
        physicsBody.setSleepingAllowed(false);
        physicsBody.setTransform(Main.WIDTH / 2f / Main.PPM, Main.HEIGHT / 2f / Main.PPM, 0f);

        PHYSICS_WORLD = world;
    }

    public void update() {
        updateMovement();
    }

    // FIXME: Bodies should probably store that as their user data
    public void updateSprite() {
        // Set the sprite's position to the body's
        Vector2 position = physicsBody.getPosition().cpy().scl(Main.PPM).sub(sprite.getWidth() / 2f, sprite.getHeight() / 2f);
        sprite.setPosition(position.x, position.y);
        sprite.setOriginCenter();
        sprite.setRotation(MathUtils.radiansToDegrees * physicsBody.getAngle());
    }

    public void updateMovement() {
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            physicsBody.applyForceToCenter(-1f, 0f, true);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            physicsBody.applyForceToCenter(1f, 0f, true);
        }

        // Clamp the velocity of the player
        Vector2 velocity = physicsBody.getLinearVelocity();
        velocity.x = MathUtils.clamp(velocity.x, -MAX_VELOCITY_X, MAX_VELOCITY_X);
        physicsBody.setLinearVelocity(velocity);

        canJump = false;
        PHYSICS_WORLD.rayCast(RAY_CALLBACK, physicsBody.getPosition().cpy().sub(0f, RADIUS), physicsBody.getPosition().cpy().sub(0f, RADIUS + 0.1f));
        if (canJump && Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            physicsBody.applyForceToCenter(0f, 1f, true);
        }
    }
}
