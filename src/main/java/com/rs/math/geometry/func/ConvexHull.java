package com.rs.math.geometry.func;

import com.rs.math.geometry.Constants;
import com.rs.math.geometry.shape.Point;
import com.rs.math.geometry.shape.Polygon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ConvexHull {
    public static Polygon convexHull_JarvisMarch(Collection<Point> points) {
        Point[] array = points.toArray(new Point[points.size()]);

        int start = 0;
        for (int i = 1; i < array.length; i++) {
            if (compare(array[start], array[i]) < 0) {
                start = i;
            }
        }

        List<Point> r = new ArrayList<>();
        r.add(array[start]);

        for (int m = 1; true; ++m) {
            int next = start;
            for (int i = 0; i < array.length; i++) {
                Point point = array[i];
                float c = crossDirection(r.get(m - 1), point, array[next]);
                if (c > Constants.EPSILON || Math.abs(c) < Constants.EPSILON && far(r.get(m - 1), point, array[next])) {
                    next = i;
                }
            }
            if (next == start) break;
            r.add(array[next]);
        }

        return new Polygon(array);
    }

    private static float cross(Point o, Point a, Point b) {
        return (a.x - o.x) * (b.y - o.y) - (a.y - o.y) * (b.x - o.x);
    }
    private static float crossDirection(Point o, Point a, Point b) {
        float r = cross(o, a, b);
        float sqrt = (float) Math.sqrt(length2(o, a) * length2(o, b));
        if (sqrt < Constants.EPSILON) return 0;
        return r / sqrt;
    }
    private static float length2(Point a, Point b) {
        return (a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y);
    }
    private static boolean far(Point o, Point a, Point b) {
        return length2(o, a) > length2(o, b);
    }

    private static int compare(float a, float b) {
        if (a < (b - Constants.EPSILON)) return -1;
        if (a > (b + Constants.EPSILON)) return 1;
        return 0;
    }

    private static int compare(Point a, Point b) {
        int c = compare(a.y, b.y);
        return c != 0 ? c : compare(a.x, b.x);
    }
}
