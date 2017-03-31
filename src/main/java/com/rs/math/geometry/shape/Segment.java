package com.rs.math.geometry.shape;

import com.rs.math.geometry.func.Distance;
import com.rs.math.geometry.value.Normal;

public class Segment {
    public final Point a;
    public final Point b;
    public Segment(Point a, Point b) {
        this.a = a;
        this.b = b;
    }

    public Line getLine() {
        return new Line(a, new Normal(a, b));
    }

    public double length() {
        return Distance.distance(a, b);
    }

    @Override
    public String toString() {
        return String.format("(%f, %f) - (%f, %f)", a.x, a.y, b.x, b.y);
    }
}
