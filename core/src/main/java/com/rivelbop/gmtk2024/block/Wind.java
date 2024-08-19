package com.rivelbop.gmtk2024.block;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Wind extends Rectangle {
    public final Vector2 DIRECTION_STRENGTH;

    public Wind(float x, float y, float width, float height, Vector2 directionStrength) {
        super(x, y, width, height);
        this.DIRECTION_STRENGTH = directionStrength;
    }
}
