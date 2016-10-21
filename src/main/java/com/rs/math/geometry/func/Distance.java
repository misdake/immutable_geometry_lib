package com.rs.math.geometry.func;

import com.rs.math.geometry.shape.Line;
import com.rs.math.geometry.shape.Point;
import com.rs.math.geometry.value.Vector;

public class Distance {
    public static float get(Point a, Point b) {
        float dx = a.x - b.x;
        float dy = a.y - b.y;
        return (float) Math.hypot(dx, dy);
    }

    public static float getSqr(Point a, Point b) {
        float dx = a.x - b.x;
        float dy = a.y - b.y;
        return dx * dx + dy * dy;
    }

    public static float get(Point p, Line l) {
        Vector t = new Vector(p, l.point);
        return Math.abs(Vector.cross(t, l.direction));
    }
}
