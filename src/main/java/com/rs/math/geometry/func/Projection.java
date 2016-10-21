package com.rs.math.geometry.func;

import com.rs.math.geometry.shape.Line;
import com.rs.math.geometry.shape.Point;
import com.rs.math.geometry.value.Vector;

public class Projection {
    public static Point project(Point from, Line to) {
        Vector v = new Vector(from, to.point);
        float d = Vector.dot(v, to.direction);
        float dx = d * to.direction.x;
        float dy = d * to.direction.y;
        return new Point(to.point.x + dx, to.point.y + dy);
    }
}
