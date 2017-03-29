package com.rs.math.geometry.shape;

public class Triangle {
    public final Point   a;
    public final Point   b;
    public final Point   c;
    public final Segment ab;
    public final Segment bc;
    public final Segment ca;

    public Triangle(Point a, Point b, Point c) {
        this.a = a;
        this.b = b;
        this.c = c;
        ab = new Segment(a, b);
        bc = new Segment(b, c);
        ca = new Segment(c, a);
    }

    public Triangle(Point a, Point b, Point c, Segment ab, Segment bc, Segment ca) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.ab = ab;
        this.bc = bc;
        this.ca = ca;
    }
}
