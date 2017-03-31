package com.rs.math.geometry.func;

import com.rs.math.geometry.shape.Line;
import com.rs.math.geometry.shape.Point;
import com.rs.math.geometry.value.Vector;

public class Projection {
    public static Point project(Point from, Line to) {
        Vector v = new Vector(to.point, from);
        double d = Vector.dot(v, to.direction);
        double dx = d * to.direction.x;
        double dy = d * to.direction.y;
        return new Point(to.point.x + dx, to.point.y + dy);
    }
}
