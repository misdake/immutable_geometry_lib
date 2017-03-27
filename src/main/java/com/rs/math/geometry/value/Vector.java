package com.rs.math.geometry.value;

import com.rs.math.geometry.shape.Point;

public class Vector {
    public final float x;
    public final float y;
    Vector(float x, float y, float length) {
        float l = length / (float) Math.hypot(x, y);
        this.x = x * l;
        this.y = y * l;
    }
    public Vector(float x, float y) {
        this.x = x;
        this.y = y;
    }
    public Vector(Point from, Point to) {
        this.x = to.x - from.x;
        this.y = to.y - from.y;
    }

    public float length() {
        return (float) Math.hypot(x, y);
    }

    public static float cross(Vector a, Vector b) {
        return a.x * b.y - a.y * b.x;
    }
    public static float dot(Vector a, Vector b) {
        return a.x * b.x + a.y * b.y;
    }
}
