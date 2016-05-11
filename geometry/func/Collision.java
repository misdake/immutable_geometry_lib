package geometry.func;

import geometry.shape.Circle;
import geometry.shape.Line;
import geometry.shape.Point;

public class Collision {

    public static boolean test(Point p, Circle c) {
        return Distance.getSqr(p, c.center) < c.radius * c.radius;
    }

    public static boolean test(Circle a, Circle b) {
        float r = a.radius + b.radius;
        return Distance.getSqr(a.center, b.center) < r * r;
    }

    public static boolean test(Line l, Circle c) {
        float d = Distance.get(c.center, l);
        return d < c.radius;
    }

}
