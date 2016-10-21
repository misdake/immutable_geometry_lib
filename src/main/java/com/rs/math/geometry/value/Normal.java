package com.rs.math.geometry.value;

import com.rs.math.geometry.shape.Point;

public class Normal extends Vector {
    public Normal(float x, float y) {
        super(x / (float) Math.hypot(x, y), y / (float) Math.hypot(x, y));
    }

    public Normal(Point from, Point to) {
        this(to.x - from.x, to.y - from.y);
    }
}
