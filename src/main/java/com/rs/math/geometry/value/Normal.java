package com.rs.math.geometry.value;

import com.rs.math.geometry.shape.Point;

public class Normal extends Vector {
    public Normal(double x, double y) {
        super(x, y, 1);
    }

    public Normal(Point from, Point to) {
        this(to.x - from.x, to.y - from.y);
    }
}
