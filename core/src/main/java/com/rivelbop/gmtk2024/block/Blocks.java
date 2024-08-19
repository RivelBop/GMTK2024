package com.rivelbop.gmtk2024.block;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.rivelbop.rivelworks.io.Assets;

public enum Blocks {
    CRATE("crate.png"),
    METAL_CRATE("metalCrate.png");

    private final String TEXTURE_NAME;
    private Texture texture;

    Blocks(String texture) {
        this.TEXTURE_NAME = texture;
    }

    public static void loadTexturesToAssets(Assets assets) {
        for (Blocks b : Blocks.values()) {
            assets.load(b.TEXTURE_NAME, Texture.class);
        }
    }

    public static void loadAssetsToTextures(Assets assets) {
        for (Blocks b : Blocks.values()) {
            b.texture = assets.get(b.TEXTURE_NAME, Texture.class);
        }
    }

    public Block create(World world, Vector2 position) {
        return new Block(world, texture, position);
    }

    public Block create(World world, float x, float y) {
        return new Block(world, texture, new Vector2(x, y));
    }
}
