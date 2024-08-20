package com.rivelbop.gmtk2024;

import com.badlogic.gdx.Gdx;
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
                BodyData bodyData = (BodyData) collidingBody.getUserData();

                if (bodyData.canKill) {
                    PLAYER.isKilled = true;
                } else if (bodyData.isEnemy) {
                    PLAYER.physicsBody.applyForceToCenter(bodyData.push, true);
                    PLAYER.isFlung = true;
                } else if (bodyData.canToss) {
                    PLAYER.hittingBody = collidingBody;
                }
            }
        }

        @Override
        public void endContact(Contact contact) {
            boolean isFixtureA = contact.getFixtureA().getBody() == PLAYER.physicsBody;
            boolean isFixtureB = contact.getFixtureB().getBody() == PLAYER.physicsBody;
            if (!isFixtureA && !isFixtureB) {
                return;
            }

            Body collidingBody =
                (!isFixtureA) ? contact.getFixtureA().getBody() :
                    (!isFixtureB) ? contact.getFixtureB().getBody() : null;

            if (collidingBody == PLAYER.hittingBody) {
                PLAYER.hittingBody = null;
                return;
            }

            assert collidingBody != null;
            if (collidingBody.getUserData() instanceof BodyData) {
                BodyData bodyData = (BodyData) collidingBody.getUserData();
                if (bodyData.isEnemy) {
                    PLAYER.isFlung = true;
                }
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
        private final Player PLAYER;
        private final Body BODY;
        public final Sprite SPRITE;

        public boolean canToss, canKill, isEnemy;
        public Vector2 push;
        private float movementTimer;
        private byte direction = 1;

        private final RayCastCallback rayCastCallback = new RayCastCallback() {
            @Override
            public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
                assert PLAYER != null;
                if (fixture.getBody() == PLAYER.physicsBody) {
                    BODY.setType(BodyDef.BodyType.DynamicBody);
                }
                return 0;
            }
        };

        public String tag = "";

        public BodyData(Body body, Sprite sprite) {
            this.PLAYER = null;
            this.BODY = body;
            this.SPRITE = sprite;
        }

        public BodyData(Body body, Sprite sprite, Player player) {
            this.PLAYER = player;
            this.BODY = body;
            this.SPRITE = sprite;
        }

        public void update() {
            // Set the sprite's position to the body's
            Vector2 position = BODY.getPosition().cpy().scl(Physics.PPM).sub(SPRITE.getWidth() / 2f, SPRITE.getHeight() / 2f);
            SPRITE.setPosition(position.x, position.y);
            SPRITE.setOriginCenter();
            SPRITE.setRotation(MathUtils.radiansToDegrees * BODY.getAngle());

            if (isEnemy) {
                movementTimer += Gdx.graphics.getDeltaTime();
                if (movementTimer >= 1.5f) {
                    direction *= -1;
                    movementTimer = 0f;
                }
                BODY.setLinearVelocity(direction * 3f, BODY.getLinearVelocity().y);
            }

            if (PLAYER != null) {
                BODY.getWorld().rayCast(rayCastCallback, BODY.getPosition(), BODY.getPosition().cpy().sub(0f, 100f));
            }
        }

        public void render(SpriteBatch batch) {
            SPRITE.draw(batch);
        }
    }
}
