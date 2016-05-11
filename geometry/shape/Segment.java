package geometry.shape;

import geometry.value.Normal;

public class Segment {
    public final Point a;
    public final Point b;
    public Segment(Point a, Point b) {
        this.a = a;
        this.b = b;
    }
    public Line getLine() {
        return new Line(a, new Normal(a, b));
    }
}
