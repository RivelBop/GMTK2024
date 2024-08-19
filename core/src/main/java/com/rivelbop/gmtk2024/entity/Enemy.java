package com.rivelbop.gmtk2024.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.rivelbop.gmtk2024.Physics;
import com.rivelbop.rivelworks.g2d.physics.body.DynamicBody2D;

public class Enemy {
    private final float
        WIDTH, HEIGHT, PHYS_WIDTH, PHYS_HEIGHT;
    private Body physicsBody;

    public Enemy() {
        physicsBody = null;
        WIDTH = 0f;
        HEIGHT = 0f;
        PHYS_WIDTH = 0f;
        PHYS_HEIGHT = 0f;
    }

    public Enemy(World world, Texture texture, Vector2 push, Vector2 dimensions, Vector2 position) {
        WIDTH = dimensions.x;
        HEIGHT = dimensions.y;
        PHYS_WIDTH = WIDTH / 2f / Physics.PPM;
        PHYS_HEIGHT = HEIGHT / 2f / Physics.PPM;

        physicsBody = new DynamicBody2D(
            world,
            new PolygonShape() {{setAsBox(PHYS_WIDTH, PHYS_HEIGHT);}},
            1f,
            1f,
            0f
        ).getBody();
        physicsBody.setFixedRotation(true);
        physicsBody.setTransform(position.x / Physics.PPM + PHYS_WIDTH / 2f, position.y / Physics.PPM + PHYS_HEIGHT / 2f, 0f);

        Sprite sprite = new Sprite(texture);
        sprite.setSize(WIDTH, HEIGHT);
        Physics.BodyData bodyData = new Physics.BodyData(physicsBody, sprite);
        bodyData.isEnemy = true;
        bodyData.push = push;
        bodyData.update();
        physicsBody.setUserData(bodyData);
    }
}
