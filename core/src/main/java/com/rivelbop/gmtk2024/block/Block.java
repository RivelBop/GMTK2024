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
    private static final float
        WIDTH = 160f, HEIGHT = 160f,
        PHYS_WIDTH = WIDTH / 2f / Physics.PPM, PHYS_HEIGHT = HEIGHT / 2f / Physics.PPM;
    public Body physicsBody;

    public Block() {
        physicsBody = null;
    }

    public Block(World world, Texture texture, Vector2 position) {
        physicsBody = new DynamicBody2D(
            world,
            new PolygonShape() {{
                setAsBox(PHYS_WIDTH, PHYS_HEIGHT);
            }},
            1f,
            2f,
            0f
        ).getBody();
        physicsBody.setFixedRotation(true);
        physicsBody.setTransform(position.x / Physics.PPM + PHYS_WIDTH / 2f, position.y / Physics.PPM + PHYS_HEIGHT / 2f, 0f);

        Sprite sprite = new Sprite(texture);
        sprite.setSize(WIDTH, HEIGHT);
        Physics.BodyData bodyData = new Physics.BodyData(physicsBody, sprite);
        bodyData.canToss = true;
        bodyData.update();
        physicsBody.setUserData(bodyData);
    }
}
