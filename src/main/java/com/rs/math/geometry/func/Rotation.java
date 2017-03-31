package com.rs.math.geometry.func;

import com.rs.math.geometry.shape.Point;

public class Rotation {

    public static Point rotate(Point from, Point center, double rotation) {
        return rotate(from.x, from.y, center.x, center.y, rotation);
    }
    public static Point rotate(double fromX, double fromY, Point center, double rotation) {
        return rotate(fromX, fromY, center.x, center.y, rotation);
    }
    public static Point rotate(Point from, double centerX, double centerY, double rotation) {
        return rotate(from.x, from.y, centerX, centerY, rotation);
    }
    public static Point rotate(double fromX, double fromY, double centerX, double centerY, double rotation) {
        double x = fromX - centerX;
        double y = fromY - centerY;
        double sin = Math.sin(rotation);
        double cos = Math.cos(rotation);
        return new Point(centerX + x * cos + y * sin, centerY - x * sin + y * cos);
    }

}
