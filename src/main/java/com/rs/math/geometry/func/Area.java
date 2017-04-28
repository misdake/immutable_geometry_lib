package com.rs.math.geometry.func;

import com.rs.math.geometry.shape.Point;
import com.rs.math.geometry.shape.Polygon;

import java.util.List;

public class Area {

    public static double compute(Polygon polygon) {
        List<Point> points = polygon.points;
        double total = 0;
        for (int i = 0; i < points.size(); i++) {
            Point p1 = points.get(i);
            Point p2 = points.get((i + 1) % points.size());
            total += p1.x * p2.y - p2.x * p1.y;
        }
        double area = total / 2;
        return Math.abs(area);
    }

}
