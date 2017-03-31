package com.rs.math.geometry.func;

import com.rs.math.geometry.shape.Point;
import com.rs.math.geometry.shape.Segment;

public class Interpolation {
    public static Point lerp(Point a, Point b, double v) {
        return new Point(a.x * (1 - v) + b.x * v, a.y * (1 - v) + b.y * v);
    }

    public static Point lerp(Segment s, double v) {
        return lerp(s.a, s.b, v);
    }
}
