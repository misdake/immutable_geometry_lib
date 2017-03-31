package com.rs.math.geometry.func;

import com.rs.math.geometry.shape.Line;
import com.rs.math.geometry.shape.Point;
import com.rs.math.geometry.shape.Segment;
import com.rs.math.geometry.value.Vector;

public class Distance {
    public static double distance(Point a, Point b) {
        double dx = a.x - b.x;
        double dy = a.y - b.y;
        return Math.hypot(dx, dy);
    }

    public static double distanceSqr(Point a, Point b) {
        double dx = a.x - b.x;
        double dy = a.y - b.y;
        return dx * dx + dy * dy;
    }

    public static double distance(Point p, Line l) {
        Vector t = new Vector(p, l.point);
        return Math.abs(Vector.cross(t, l.direction));
    }

    public static double distance(Point p, Segment s) {
        double length = s.length();
        Line l = s.getLine();
        Vector t = new Vector(l.point, p); //l.point就是s.a
        double dot = Vector.dot(t, l.direction);
        if (dot < 0) return distance(p, s.a);
        if (dot > length) return distance(p, s.b);
        double cross = Vector.cross(t, l.direction);
        return Math.abs(cross);
    }
}
