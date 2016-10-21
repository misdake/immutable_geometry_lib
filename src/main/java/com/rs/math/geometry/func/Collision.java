package com.rs.math.geometry.func;

import com.rs.math.geometry.shape.Line;
import com.rs.math.geometry.shape.Point;
import com.rs.math.geometry.shape.Circle;

public class Collision {

    public static boolean test(Point p, Circle c) {
        return Distance.distanceSqr(p, c.center) < c.radius * c.radius;
    }

    public static boolean test(Circle a, Circle b) {
        float r = a.radius + b.radius;
        return Distance.distanceSqr(a.center, b.center) < r * r;
    }

    public static boolean test(Line l, Circle c) {
        float d = Distance.distance(c.center, l);
        return d < c.radius;
    }

}
