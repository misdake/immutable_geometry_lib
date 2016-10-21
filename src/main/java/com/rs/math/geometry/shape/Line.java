package com.rs.math.geometry.shape;

import com.rs.math.geometry.value.Normal;

public class Line {
    public final Point  point;
    public final Normal direction;
    public Line(Point point, Normal direction) {
        this.point = point;
        this.direction = direction;
    }
}
