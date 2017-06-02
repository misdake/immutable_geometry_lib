package com.rs.math.geometry.value;

import com.rs.math.geometry.shape.Point;

public class Vector {
    public final double x;
    public final double y;
    public final double l;
    Vector(double x, double y, double length) {
        double c = length / Math.hypot(x, y);
        this.x = x * c;
        this.y = y * c;
        this.l = length;
    }
    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
        this.l = Math.hypot(x, y);
    }
    public Vector(Point from, Point to) {
        this.x = to.x - from.x;
        this.y = to.y - from.y;
        this.l = Math.hypot(x, y);
    }

    public double length() {
        return l;
    }

    public static double cross(Vector a, Vector b) {
        return a.x * b.y - a.y * b.x;
    }
    public static double dot(Vector a, Vector b) {
        return a.x * b.x + a.y * b.y;
    }
}
