package com.rivelbop.gmtk2024.block;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public enum Blocks {
    CRATE(null),
    METAL_CRATE(null);

    private final String TEXTURE_NAME;
    private Texture texture;

    Blocks(String texture) {
        this.TEXTURE_NAME = texture;
    }

    public Block create(World world, Vector2 position) {
        return new Block(world, texture, position);
    }
}
