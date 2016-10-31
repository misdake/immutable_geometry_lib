package com.rs.math.geometry.func;

import com.rs.math.geometry.Constants;
import com.rs.math.geometry.shape.Point;
import com.rs.math.geometry.shape.Polygon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

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

    public static Polygon convexHull_misdake(Collection<Point> points) {
        Point[] array = points.toArray(new Point[points.size()]);
        Arrays.sort(array, ANDREW_MONOTONE_CHAIN_COMPARATOR);

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
            float max = -1000;
            Point maxPoint = null;
            for (Point point : array) {
                if (!r.contains(point) && (r.size() > 2 || point != start)) {
                    float v = angle(prev, curr, point);
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
        Arrays.sort(array, ANDREW_MONOTONE_CHAIN_COMPARATOR);

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
            float max = -1000;
            Point maxPoint = null;
            for (Point point : graph.get(curr)) {
                if (!r.contains(point) && (r.size() > 2 || point != start)) {
                    float v = angle(prev, curr, point);
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

    private static float angle(Point v1, Point v2, Point v3) {
        // b:v1~v2,
        // c:v2~v3,
        // a:v1~v3
        boolean cw = cross(v1, v2, v3) > 0;
        float dx1 = v2.x - v1.x, dy1 = v2.y - v1.y;
        float dx2 = v3.x - v2.x, dy2 = v3.y - v2.y;
        float dx3 = dx1 - dx2, dy3 = dy1 - dy2;
        float dl1 = dx1 * dx1 + dy1 * dy1;
        float dl2 = dx2 * dx2 + dy2 * dy2;
        float dl3 = dx3 * dx3 + dy3 * dy3;

        double dl1r = Math.sqrt(dl1);
        if (dl1r == 0)
            return 0;
        double dl2r = Math.sqrt(dl2);
        if (dl2r == 0)
            return 0;

        double cosAlpha = (dl1 + dl2 - dl3) / dl1r / dl2r / 2;

        if (cosAlpha > 1)
            cosAlpha = 1;
        if (cosAlpha < -1)
            cosAlpha = -1;

        float alpha = (float) Math.acos(cosAlpha);

        if (alpha == Double.NaN)
            return 0;

        if (cw) {
            return (float) -Math.toDegrees(alpha);
        } else {
            return (float) Math.toDegrees(alpha);
        }
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
