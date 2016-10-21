package com.rs.math.geometry.shape;

public class Point {
    public final float x;
    public final float y;
    public Point(float x, float y) {
        this.x = x;
        this.y = y;
    }
    public Point(Point point) {
        this.x = point.x;
        this.y = point.y;
    }
}
