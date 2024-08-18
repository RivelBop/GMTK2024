package com.rivelbop.gmtk2024.block;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.rivelbop.gmtk2024.Physics;
import com.rivelbop.rivelworks.g2d.physics.body.DynamicBody2D;

public class Block {
    private static final float WIDTH = 50f / 2f / Physics.PPM, HEIGHT = 50f / 2f / Physics.PPM;
    public Body physicsBody;

    public Block() {
        physicsBody = null;
    }

    public Block(World world, Texture texture, Vector2 position) {
        physicsBody = new DynamicBody2D(
            world,
            new PolygonShape() {{setAsBox(WIDTH, HEIGHT);}},
            1f,
            1f,
            0f
        ).getBody();
        physicsBody.setFixedRotation(true);
        physicsBody.setTransform(position.x / Physics.PPM + WIDTH / 2f, position.y / Physics.PPM + HEIGHT / 2f, 0f);

        Sprite sprite = new Sprite(texture);
        sprite.setSize(64f, 64f);
        Physics.BodyData bodyData = new Physics.BodyData(physicsBody, sprite);
        bodyData.update();
        physicsBody.setUserData(bodyData);
    }
}
