package com.rivelbop.gmtk2024.block;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.rivelbop.gmtk2024.Physics;
import com.rivelbop.gmtk2024.entity.Player;
import com.rivelbop.rivelworks.g2d.physics.body.DynamicBody2D;
import com.rivelbop.rivelworks.io.Assets;

public class Spike {
    private static final float
        WIDTH = 96f, HEIGHT = 130f,
        PHYS_WIDTH = WIDTH / 2f / Physics.PPM, PHYS_HEIGHT = HEIGHT / 2f / Physics.PPM;
    public Body physicsBody;

    public Spike(World world, Assets assets, Vector2 position, Player player) {
        physicsBody = new DynamicBody2D(
            world,
            new PolygonShape() {{setAsBox(PHYS_WIDTH, PHYS_HEIGHT);}},
            5f,
            1f,
            0f
        ).getBody();
        physicsBody.setType(BodyDef.BodyType.StaticBody);
        physicsBody.setFixedRotation(true);
        physicsBody.setTransform(position.x / Physics.PPM + PHYS_WIDTH / 2f, position.y / Physics.PPM + PHYS_HEIGHT / 2f, 0f);

        Sprite sprite = new Sprite(assets.get("spike.png", Texture.class));
        sprite.setSize(WIDTH, HEIGHT);
        Physics.BodyData bodyData = new Physics.BodyData(physicsBody, sprite, player);
        bodyData.canKill = true;
        bodyData.update();
        physicsBody.setUserData(bodyData);
    }
}
