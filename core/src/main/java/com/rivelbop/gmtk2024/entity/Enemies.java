package com.rivelbop.gmtk2024.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.rivelbop.rivelworks.io.Assets;

public enum Enemies {
    GOAT("goat.png", -25f, 75f, 57.4f, 53.4f);

    private final String TEXTURE_NAME;
    private final Vector2 PUSH, DIMENSIONS;
    private Texture texture;

    Enemies(String texture, float pushX, float pushY, float width, float height) {
        this.TEXTURE_NAME = texture;
        this.PUSH = new Vector2(pushX, pushY);
        this.DIMENSIONS = new Vector2(width, height);
    }

    public static void loadTexturesToAssets(Assets assets) {
        for (Enemies e : Enemies.values()) {
            assets.load(e.TEXTURE_NAME, Texture.class);
        }
    }

    public static void loadAssetsToTextures(Assets assets) {
        for (Enemies e : Enemies.values()) {
            e.texture = assets.get(e.TEXTURE_NAME, Texture.class);
        }
    }

    public Enemy create(World world, Vector2 position) {
        return new Enemy(world, texture, PUSH, DIMENSIONS, position);
    }

    public Enemy create(World world, float x, float y) {
        return new Enemy(world, texture, PUSH, DIMENSIONS, new Vector2(x, y));
    }
}
