package com.rs.math.geometry.func;

import com.rs.math.geometry.Constants;
import com.rs.math.geometry.shape.Point;

import java.util.Comparator;

class L {
    final static Comparator<Point> POINT_COMPARATOR_LEFT_BOTTOM = new Comparator<Point>() {
        @Override
        public int compare(Point a, Point b) {
            int c = Float.compare(a.x, b.x);
            return c != 0 ? c : Float.compare(a.y, b.y);
        }
    };

    static float angle(Point v1, Point v2, Point v3) {
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

    static float cross(Point o, Point a, Point b) {
        return (a.x - o.x) * (b.y - o.y) - (a.y - o.y) * (b.x - o.x);
    }

    static float crossDirection(Point o, Point a, Point b) {
        float r = cross(o, a, b);
        float sqrt = (float) Math.sqrt(length2(o, a) * length2(o, b));
        if (sqrt < Constants.EPSILON) return 0;
        return r / sqrt;
    }

    private static float length2(Point a, Point b) {
        return (a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y);
    }

    static boolean far(Point o, Point a, Point b) {
        return length2(o, a) > length2(o, b);
    }

    private static int compare(float a, float b) {
        if (a < (b - Constants.EPSILON)) return -1;
        if (a > (b + Constants.EPSILON)) return 1;
        return 0;
    }

    static int compare(Point a, Point b) {
        int c = compare(a.y, b.y);
        return c != 0 ? c : compare(a.x, b.x);
    }
}
