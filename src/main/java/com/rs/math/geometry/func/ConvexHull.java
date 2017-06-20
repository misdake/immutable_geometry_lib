package com.rs.math.geometry.func;

import com.rs.math.geometry.Constants;
import com.rs.math.geometry.shape.Point;
import com.rs.math.geometry.shape.Polygon;

import java.util.*;

public class ConvexHull {
    public static Polygon convexHull_JarvisMarch(Collection<Point> points) {
        Point[] array = points.toArray(new Point[points.size()]);

        int start = 0;
        for (int i = 1; i < array.length; i++) {
            if (L.compare(array[start], array[i]) < 0) {
                start = i;
            }
        }

        List<Point> r = new ArrayList<>();
        r.add(array[start]);

        for (int m = 1; true; ++m) {
            int next = start;
            for (int i = 0; i < array.length; i++) {
                Point point = array[i];
                double c = L.crossDirection(r.get(m - 1), point, array[next]);
                if (c > Constants.EPSILON || Math.abs(c) < Constants.EPSILON && L.far(r.get(m - 1), point, array[next])) {
                    next = i;
                }
            }
            if (next == start) break;
            r.add(array[next]);
        }

        return new Polygon(array);
    }

    public static Polygon convexHull_AndrewMonotoneChain(Collection<Point> points) {
        Point[] P = points.toArray(new Point[0]);
        Point[] L = new Point[P.length];
        Point[] U = new Point[P.length];

        Arrays.sort(P, com.rs.math.geometry.func.L.POINT_COMPARATOR_LEFT_BOTTOM);

        int l = 0, u = 0;   // 下凸包的點數、上凸包的點數
        for (int i = 0; i < P.length; ++i) {
            while (l >= 2 && com.rs.math.geometry.func.L.cross(L[l - 2], L[l - 1], P[i]) <= 0) l--;
            while (u >= 2 && com.rs.math.geometry.func.L.cross(U[u - 2], U[u - 1], P[i]) >= 0) u--;
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

    public static Polygon convexHull_misdake(Collection<Point> points) {
        Point[] array = points.toArray(new Point[points.size()]);
        Arrays.sort(array, L.POINT_COMPARATOR_LEFT_BOTTOM);

        List<Point> r = new ArrayList<>();
        Point prev = array[0];
        Point curr = array[1];
        if (prev.y < curr.y) {
            Point t = prev;
            prev = curr;
            curr = t;
        }
        r.add(curr);
        Point start = prev;

        for (; ; ) {
            double max = -1000;
            Point maxPoint = null;
            for (Point point : array) {
                if (!r.contains(point) && (r.size() >= 2 || point != start)) {
                    double v = L.angle(prev, curr, point);
                    if (v > max) {
                        max = v;
                        maxPoint = point;
                    }
                }
            }
            if (maxPoint == start) break;
            r.add(maxPoint);
            prev = curr;
            curr = maxPoint;
        }

        r.add(start);
        return new Polygon(r);
    }
    public static Polygon convexHull_misdake(Collection<Point> points, Map<Point, List<Point>> graph) {
        Point[] array = points.toArray(new Point[points.size()]);
        Arrays.sort(array, L.POINT_COMPARATOR_LEFT_BOTTOM);

        List<Point> r = new ArrayList<>();
        Point prev = array[0];
        Point curr = array[1];
        if (prev.y < curr.y) {
            Point t = prev;
            prev = curr;
            curr = t;
        }
        r.add(curr);
        Point start = prev;

        List<Point> bad = new ArrayList<>();

        for (; ; ) {
            double max = -1000;
            Point maxPoint = null;
            if (curr == null) return null;
            List<Point> nextSet = graph.get(curr);
            if (nextSet == null) return null;
            for (Point point : nextSet) {
                if (!bad.contains(point) && !r.contains(point) && point != prev && (r.size() >= 2 || point != start)) {
                    double v = L.angle(prev, curr, point);
                    if (v > max) {
                        max = v;
                        maxPoint = point;
                    }
                }
            }
            if (maxPoint == null) {
                r.remove(curr);
                bad.add(curr);
                if (r.size() > 1) {
                    prev = r.get(r.size() - 2);
                    curr = r.get(r.size() - 1);
                } else {
                    prev = start;
                    curr = r.get(r.size() - 1);
                }
            } else {
                if (maxPoint == start) break;
                r.add(maxPoint);
                prev = curr;
                curr = maxPoint;
            }
        }

        r.add(start);
        return new Polygon(r);
    }

}
