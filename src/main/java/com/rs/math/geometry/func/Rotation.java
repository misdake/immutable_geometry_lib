package com.rs.math.geometry.func;

import com.rs.math.geometry.shape.Point;

public class Rotation {

    public static Point rotate(Point from, Point center, float rotation) {
        return rotate(from.x, from.y, center.x, center.y, rotation);
    }
    public static Point rotate(float fromX, float fromY, Point center, float rotation) {
        return rotate(fromX, fromY, center.x, center.y, rotation);
    }
    public static Point rotate(Point from, float centerX, float centerY, float rotation) {
        return rotate(from.x, from.y, centerX, centerY, rotation);
    }
    public static Point rotate(float fromX, float fromY, float centerX, float centerY, float rotation) {
        float x = fromX - centerX;
        float y = fromY - centerY;
        float sin = (float) Math.sin(rotation);
        float cos = (float) Math.cos(rotation);
        return new Point(centerX + x * cos + y * sin, centerY - x * sin + y * cos);
    }

}
