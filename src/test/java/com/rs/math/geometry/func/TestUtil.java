package com.rs.math.geometry.func;

import com.rs.math.geometry.Constants;
import com.rs.math.geometry.shape.Point;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;

public class TestUtil {
    public static void polygonEqual(List<Point> points, List<Point> referenceResult) {
        List<Point> t = new ArrayList<>(points);
        for (Point point : referenceResult) {
            Point x = nearestPoint(point, t);
            assertNotNull(x);
            t.remove(x);
            assertTrue(Distance.distance(point, x) < Constants.EPSILON);
        }
    }
    public static Point nearestPoint(Point p, Collection<Point> points) {
        Point min = null;
        double minD = Double.MAX_VALUE;
        for (Point point : points) {
            double d = Distance.distance(p, point);
            if (d < minD) {
                minD = d;
                min = point;
            }
        }
        return min;
    }
}
