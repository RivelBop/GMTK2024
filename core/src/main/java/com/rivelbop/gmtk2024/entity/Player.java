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
import com.rivelbop.gmtk2024.Physics;
import com.rivelbop.rivelworks.g2d.physics.body.DynamicBody2D;
import com.rivelbop.rivelworks.io.Assets;

public class Player {
    private final float MAX_VELOCITY_X = 2f, RADIUS = 25f / Physics.PPM;
    private final World PHYSICS_WORLD;
    public Body physicsBody, hittingBody, standingBody;
    protected boolean canJump;

    private final RayCastCallback RAY_CALLBACK =
        (fixture, point, normal, fraction) -> {
            standingBody = fixture.getBody();
            canJump = true;
            return 0;
        };

    public Player(Assets assets, World world) {
        Sprite sprite = new Sprite(assets.get("goat.png", Texture.class));
        sprite.setSize(sprite.getWidth() * 0.1f, sprite.getHeight() * 0.1f);

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
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            physicsBody.applyForceToCenter(-1f, 0f, true);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            physicsBody.applyForceToCenter(1f, 0f, true);
        }

        // Clamp the velocity of the player
        Vector2 velocity = physicsBody.getLinearVelocity();
        velocity.x = MathUtils.clamp(velocity.x, -MAX_VELOCITY_X, MAX_VELOCITY_X);

        canJump = false;
        PHYSICS_WORLD.rayCast(RAY_CALLBACK, physicsBody.getPosition().cpy().sub(0f, RADIUS), physicsBody.getPosition().cpy().sub(0f, RADIUS + 0.1f));
        if (canJump && Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            velocity.y = 2f;
        }
        physicsBody.setLinearVelocity(velocity);
    }

    private void moveBody() {
        if (hittingBody != null) {
            Vector2 velocity = hittingBody.getLinearVelocity();
            if (canJump && standingBody != hittingBody && Gdx.input.isKeyPressed(Input.Keys.W)) {
                velocity.y = 2f;
            }
            hittingBody.setLinearVelocity(velocity);
        }
    }
}
