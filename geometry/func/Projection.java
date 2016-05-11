package geometry.func;

import geometry.shape.Line;
import geometry.shape.Point;
import geometry.value.Vector;

public class Projection {
    public static Point doit(Point from, Line to) {
        Vector v = new Vector(from, to.point);
        float d = Vector.dot(v, to.direction);
        float dx = d * to.direction.x;
        float dy = d * to.direction.y;
        return new Point(to.point.x + dx, to.point.y + dy);
    }
}
