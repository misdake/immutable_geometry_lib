package com.rs.math.geometry.func;

import com.rs.math.geometry.Constants;
import com.rs.math.geometry.shape.Point;
import com.rs.math.geometry.shape.Polygon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class ConvexHull {
    public static Polygon convexHull_JarvisMarch(Collection<Point> points) {
        Point[] array = points.toArray(new Point[points.size()]);
        final List<Point> list = Arrays.asList(array);
        return convexHull_JarvisMarch(array, new NextPossible_JarvisMarch() {
            @Override
            public Collection<Point> getNext(Point point) {
                return list;
            }
        });
    }

    private static Polygon convexHull_JarvisMarch(Point[] array, NextPossible_JarvisMarch nextPossible) {

        int start = 0;
        for (int i = 1; i < array.length; i++) {
            if (compare(array[start], array[i]) < 0) {
                start = i;
            }
        }

        List<Point> r = new ArrayList<>();
        r.add(array[start]);

        for (int m = 0; true; ++m) {
            Point curr = r.get(m);
            Point next = array[start];
            Collection<Point> possible = nextPossible.getNext(curr);
            for (Point point : possible) {
                float c = crossDirection(curr, point, next);
                if (c > Constants.EPSILON || Math.abs(c) < Constants.EPSILON && far(curr, point, next)) {
                    next = point;
                }
            }
            if (next == array[start]) break;
            r.add(next);
        }

        return new Polygon(array);
    }
    public interface NextPossible_JarvisMarch {
        Collection<Point> getNext(Point point);
    }

    public static Polygon convexHull_AndrewMonotoneChain(Collection<Point> points) {
        Point[] P = points.toArray(new Point[0]);
        Point[] L = new Point[P.length];
        Point[] U = new Point[P.length];

        Arrays.sort(P, ANDREW_MONOTONE_CHAIN_COMPARATOR);

        int l = 0, u = 0;   // 下凸包的點數、上凸包的點數
        for (int i = 0; i < P.length; ++i) {
            while (l >= 2 && cross(L[l - 2], L[l - 1], P[i]) <= 0) l--;
            while (u >= 2 && cross(U[u - 2], U[u - 1], P[i]) >= 0) u--;
            L[l++] = P[i];
            U[u++] = P[i];
        }

        List<Point> list = new ArrayList<>();
        for (int i = 0; i < l; i++) {
            list.add(L[i]);
        }
        for (int i = u - 2; i >= 1; i--) {
            list.add(U[i]);
        }

        return new Polygon(list);
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

    private final static Comparator<Point> ANDREW_MONOTONE_CHAIN_COMPARATOR = new Comparator<Point>() {
        @Override
        public int compare(Point a, Point b) {
            int c = Float.compare(a.x, b.x);
            return c != 0 ? c : Float.compare(a.y, b.y);
        }
    };
}
