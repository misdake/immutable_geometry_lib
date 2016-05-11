package geometry.func;

import geometry.shape.Point;
import geometry.shape.Segment;

public class Interpolation {
    public static Point lerp(Point a, Point b, float v) {
        return new Point(a.x * (1 - v) + b.x * v, a.y * (1 - v) + b.y * v);
    }

    public static Point lerp(Segment s, float v) {
        return lerp(s.a, s.b, v);
    }
}
