package com.rivelbop.gmtk2024;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.rivelbop.gmtk2024.entity.Player;

public class Physics {
    public static final float PPM = 100f;

    public static class PhysicsListener implements ContactListener {
        private final World WORLD;
        private final Player PLAYER;

        public PhysicsListener(World world, Player player) {
            this.WORLD = world;
            this.PLAYER = player;
        }

        @Override
        public void beginContact(Contact contact) {
            boolean isFixtureA = contact.getFixtureA().getBody() == PLAYER.physicsBody;
            boolean isFixtureB = contact.getFixtureB().getBody() == PLAYER.physicsBody;
            if (!isFixtureA && !isFixtureB) {
                return;
            }

            Body collidingBody =
                (!isFixtureA) ? contact.getFixtureA().getBody() :
                    (!isFixtureB) ? contact.getFixtureB().getBody() : null;

            assert collidingBody != null;
            if (collidingBody.getUserData() instanceof Physics.BodyData) {
                PLAYER.hittingBody = collidingBody;
            }
        }

        @Override
        public void endContact(Contact contact) {
            if (contact.getFixtureA().getBody() == PLAYER.hittingBody ||
            contact.getFixtureB().getBody() == PLAYER.hittingBody) {
                PLAYER.hittingBody = null;
            }
        }

        @Override
        public void preSolve(Contact contact, Manifold oldManifold) {
        }

        @Override
        public void postSolve(Contact contact, ContactImpulse impulse) {
        }
    }

    public static class BodyData {
        private final Body BODY;
        public final Sprite SPRITE;

        public BodyData(Body body, Sprite sprite) {
            this.BODY = body;
            this.SPRITE = sprite;
        }

        public void update() {
            // Set the sprite's position to the body's
            Vector2 position = BODY.getPosition().cpy().scl(Physics.PPM).sub(SPRITE.getWidth() / 2f, SPRITE.getHeight() / 2f);
            SPRITE.setPosition(position.x, position.y);
            SPRITE.setOriginCenter();
            SPRITE.setRotation(MathUtils.radiansToDegrees * BODY.getAngle());
        }

        public void render(SpriteBatch batch) {
            SPRITE.draw(batch);
        }
    }
}
