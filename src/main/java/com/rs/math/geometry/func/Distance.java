package com.rs.math.geometry.func;

import com.rs.math.geometry.shape.Line;
import com.rs.math.geometry.shape.Point;
import com.rs.math.geometry.shape.Segment;
import com.rs.math.geometry.value.Vector;

public class Distance {
    public static float distance(Point a, Point b) {
        float dx = a.x - b.x;
        float dy = a.y - b.y;
        return (float) Math.hypot(dx, dy);
    }

    public static float distanceSqr(Point a, Point b) {
        float dx = a.x - b.x;
        float dy = a.y - b.y;
        return dx * dx + dy * dy;
    }

    public static float distance(Point p, Line l) {
        Vector t = new Vector(p, l.point);
        return Math.abs(Vector.cross(t, l.direction));
    }

    public static float distance(Point p, Segment s) {
        float length = s.length();
        Line l = s.getLine();
        Vector t = new Vector(l.point, p); //l.point就是s.a
        float dot = Vector.dot(t, l.direction);
        if (dot < 0) return distance(p, s.a);
        if (dot > length) return distance(p, s.b);
        float cross = Vector.cross(t, l.direction);
        return Math.abs(cross);
    }
}
