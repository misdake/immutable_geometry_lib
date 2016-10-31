package com.rs.math.geometry.func;

import com.rs.math.geometry.Constants;
import com.rs.math.geometry.shape.Circle;
import com.rs.math.geometry.shape.Line;
import com.rs.math.geometry.shape.Point;
import com.rs.math.geometry.shape.Polygon;
import com.rs.math.geometry.shape.Segment;
import com.rs.math.geometry.value.Vector;

import java.util.List;

import static com.rs.math.geometry.func.Collision.SegmentResultType.CONNECTED;
import static com.rs.math.geometry.func.Collision.SegmentResultType.IN;
import static com.rs.math.geometry.func.Collision.SegmentResultType.INTERLEAVED;
import static com.rs.math.geometry.func.Collision.SegmentResultType.INTERSECTED;
import static com.rs.math.geometry.func.Collision.SegmentResultType.IN_CONNECTED;
import static com.rs.math.geometry.func.Collision.SegmentResultType.NONE;
import static com.rs.math.geometry.func.Collision.SegmentResultType.OUT;
import static com.rs.math.geometry.func.Collision.SegmentResultType.OUT_CONNECTED;
import static com.rs.math.geometry.func.Collision.SegmentResultType.SAME;

public class Collision {

    public enum SegmentResultType {
        SAME,
        IN,
        IN_CONNECTED,
        OUT,
        OUT_CONNECTED,
        INTERLEAVED,
        CONNECTED,
        INTERSECTED,
        NONE,
    }

    public static class SegmentResult {
        public final SegmentResultType resultType;
        public final Point             point;
        public final int[]             indices;
        public SegmentResult(SegmentResultType resultType) {
            this.resultType = resultType;
            this.point = null;
            indices = null;
        }
        public SegmentResult(SegmentResultType resultType, Point point) {
            this.resultType = resultType;
            this.point = point;
            indices = new int[]{-1, -1, -1, -1};
        }
        public SegmentResult(SegmentResultType resultType, int[] r, Point point) {
            this.resultType = resultType;
            this.point = point;
            indices = r;
        }
        public SegmentResult(SegmentResultType resultType, int[] r) {
            this.resultType = resultType;
            this.point = null;
            indices = r;
        }
        public SegmentResult(SegmentResultType resultType, int aa, int ab, int ba, int bb) {
            this.resultType = resultType;
            this.point = null;
            indices = new int[]{aa, ab, ba, bb};
        }
    }

    // test

    public static boolean test(Point p, Circle c) {
        return Distance.distanceSqr(p, c.center) < c.radius * c.radius;
    }

    public static boolean test(Circle a, Circle b) {
        float r = a.radius + b.radius;
        return Distance.distanceSqr(a.center, b.center) < r * r;
    }

    public static boolean test(Line l, Circle c) {
        float d = Distance.distance(c.center, l);
        return d < c.radius;
    }

    public static boolean testPossible(Segment a, Segment b) {
        Point a1 = a.a;
        Point a2 = a.b;
        Point b1 = b.a;
        Point b2 = b.b;
        Vector va = new Vector(a1, a2);
        Vector vb = new Vector(b1, b2);
        //TODO is <Constants.EPSILON enough?
        boolean ta = Vector.cross(va, new Vector(a1, b1)) * Vector.cross(va, new Vector(a1, b2)) < Constants.EPSILON;
        boolean tb = Vector.cross(vb, new Vector(b1, a1)) * Vector.cross(vb, new Vector(b1, a2)) < Constants.EPSILON;
        return ta && tb;
    }

    public static boolean test(Segment a, Segment b) {
        SegmentResult r = intersect(a, b);
        return r.resultType != NONE;
    }

    // is

    public static boolean is(Point p1, Point p2) {
        return Distance.distance(p1, p2) < Constants.EPSILON;
    }

    // in

    public static boolean in(Point p, Polygon polygon) {
        boolean c = false;

        List<Point> points = polygon.points;
        int n = points.size();
        for (int i = 0, j = n - 1; i < n; j = i++) {
            Point pi = points.get(i);
            Point pj = points.get(j);
            if (((pi.y > p.y) != (pj.y > p.y)) && (p.x < (pj.x - pi.x) * (p.y - pi.y) / (pj.y - pi.y) + pi.x))
                c = !c;
        }
        return c;
    }

    public static boolean in(Segment segment, Polygon polygon) {
        Point a = segment.a;
        Point b = segment.b;

        boolean inA = in(a, polygon);
        boolean inB = in(b, polygon);
        boolean onA = on(a, polygon);
        boolean onB = on(b, polygon);
        if ((!inA && !onA) || (!inB && !onB)) {
            return false;
        }
        List<Point> points = polygon.points;
        int size = points.size();
        for (int i = 0, j = size - 1; i < size; j = i, i++) {
            Point pi = points.get(i);
            Point pj = points.get(j);
            if (intersect(new Segment(pi, pj), segment).resultType == INTERSECTED) {
                return false;
            }
        }
        return true;
    }

    // on

    public static boolean on(Point p, Segment s) {
        float maxLength = s.length();
        Vector v = new Vector(s.a, p);
        Line line = s.getLine();
        float distance = Distance.distance(p, line);
        if (distance > Constants.EPSILON) return false;
        float dot = Vector.dot(v, line.direction);
        boolean inSegment = dot <= maxLength + Constants.EPSILON && dot >= -Constants.EPSILON;
        return inSegment;
    }
    private static boolean on_trusted(Point p, Segment s) {
        float maxLength = s.length();
        Vector v = new Vector(s.a, p);
        Line line = s.getLine();
        float dot = Vector.dot(v, line.direction);
        boolean inSegment = dot <= maxLength + Constants.EPSILON && dot >= -Constants.EPSILON;
        return inSegment;
    }
    private static float on_length(Point p, Segment s) {
        float maxLength = s.length();
        Vector v = new Vector(s.a, p);
        Line line = s.getLine();
        float dot = Vector.dot(v, line.direction);
        return dot / maxLength;
    }

    public static boolean on(Point p, Polygon polygon) {
        for (Segment segment : polygon.segments) {
            if (on(p, segment)) return true;
        }
        return false;
    }

    // intersect

    public static SegmentResult intersect(Line a, Line b) { // SAME || INTERSECTED || NONE
        Point p1 = a.point;
        Point p2 = new Point(a.point.x + a.direction.x, a.point.y + a.direction.y);
        Point ap1 = Projection.project(p1, b);
        Point ap2 = Projection.project(p2, b);
        Vector v1 = new Vector(p1, ap1);
        Vector v2 = new Vector(p2, ap2);

        float length = v1.length();
        float length2 = v2.length();
        if (length < Constants.EPSILON && length2 < Constants.EPSILON) {
            // point a is on line b => SAME
            return new SegmentResult(SAME);
        }

        float direction = Vector.dot(v1, a.direction);
        if (Math.abs(direction) < Constants.EPSILON) {
            // projection (point a -> line b) direction is perpendicular to line a => parallel => NONE
            return new SegmentResult(NONE);
        }

        float t = length * length / direction;
        float x = a.point.x + a.direction.x * t;
        float y = a.point.y + a.direction.y * t;
        return new SegmentResult(INTERSECTED, new Point(x, y));
    }

    public static SegmentResult intersect(Segment a, Line b) { // INSIDE || CONNECTED || INTERSECTED || NONE
        Line line = a.getLine();
        SegmentResult r = intersect(line, b);
        switch (r.resultType) { // SAME || INTERSECTED || NONE
            case INTERSECTED: // test
                float d1 = Distance.distance(a.a, b);
                float d2 = Distance.distance(a.b, b);
                if (Math.abs(d1) < Constants.EPSILON || Math.abs(d2) < Constants.EPSILON) {
                    return new SegmentResult(CONNECTED, r.point);
                }
                boolean intersected = on_trusted(r.point, a);
                return intersected ? r : new SegmentResult(NONE);
            case SAME:
                return new SegmentResult(IN);
            case NONE:
                return r;
        }
        throw new RuntimeException("unexpected resultType");
    }

    public static SegmentResult intersect(Segment a, Segment b) { // SAME || INSIDE || OUTSIDE || INTERLEAVED || CONNECTED || INTERSECTED || NONE
        if (!testPossible(a, b)) {
            return new SegmentResult(NONE);
        }

        Line la = a.getLine();
        Line lb = b.getLine();
        SegmentResult r = intersect(la, lb);
        switch (r.resultType) {
            case INTERSECTED: {
                //share one point?
                boolean is_aa_ba = is(a.a, b.a);
                boolean is_ab_bb = is(a.b, b.b);
                boolean is_aa_bb = is(a.a, b.b);
                boolean is_ab_ba = is(a.b, b.a);
                if (is_aa_ba) return new SegmentResult(CONNECTED, 1, 0, 1, 2);
                if (is_ab_bb) return new SegmentResult(CONNECTED, 0, 1, 2, 1);
                if (is_aa_bb) return new SegmentResult(CONNECTED, 1, 0, 2, 1);
                if (is_ab_ba) return new SegmentResult(CONNECTED, 0, 1, 1, 2);

                //test point
                boolean inA = on_trusted(r.point, a);
                boolean inB = on_trusted(r.point, b);
                return inA && inB ? r : new SegmentResult(NONE);
            }

            case SAME:
                return testSameLine(a, b);

            case NONE:
                return r;
        }
        throw new RuntimeException("unexpected resultType");
    }

    private static SegmentResult testSameLine(Segment a, Segment b) {
        float length_aa_b = on_length(a.a, b);
        float length_ab_b = on_length(a.b, b);
        float min_a = Math.min(length_aa_b, length_ab_b);
        float max_a = Math.max(length_aa_b, length_ab_b);
        boolean ab = length_aa_b < length_ab_b;

        int s1 = status(min_a, 0, 1);
        int s2 = status(max_a, 0, 1);

        SegmentResult x = getSegmentResult(s1, s2, a, b);
        if (!ab && x.indices != null) {
            int n_aa = x.indices[1];
            int n_ab = x.indices[0];
            x.indices[0] = n_aa;
            x.indices[1] = n_ab;
        }
        return x;
    }

    private static SegmentResult getSegmentResult(int s1, int s2, Segment a, Segment b) {
        switch (s1) {
            case -2:
                switch (s2) {
                    case -2:
                        return new SegmentResult(NONE);
                    case -1:
                        return new SegmentResult(CONNECTED, new int[]{0, 1, 1, 2}, b.a);
                    case 0:
                        return new SegmentResult(INTERLEAVED, new int[]{0, 2, 1, 3});
                    case 1:
                        return new SegmentResult(OUT_CONNECTED, new int[]{0, 2, 1, 2}, b.b);
                    case 2:
                        return new SegmentResult(OUT, new int[]{0, 3, 1, 2});
                }
            case -1:
                switch (s2) {
                    case 0:
                        return new SegmentResult(IN_CONNECTED, new int[]{0, 1, 0, 2}, b.a);
                    case 1:
                        return new SegmentResult(SAME);
                    case 2:
                        return new SegmentResult(OUT_CONNECTED, new int[]{0, 2, 0, 1}, b.a);
                }
            case 0:
                switch (s2) {
                    case 0:
                        return new SegmentResult(IN, new int[]{1, 2, 0, 3});
                    case 1:
                        return new SegmentResult(IN_CONNECTED, new int[]{1, 2, 0, 2}, b.b);
                    case 2:
                        return new SegmentResult(INTERLEAVED, new int[]{1, 3, 0, 2});
                }
            case 1:
                switch (s2) {
                    case 2:
                        return new SegmentResult(CONNECTED, b.b);
                }
            case 2:
                switch (s2) {
                    case 2:
                        return new SegmentResult(NONE);
                }
        }
        throw new RuntimeException("unexpected resultType");
    }

    private static int status(float f, float a, float b) {
        if (Math.abs(f - a) < Constants.EPSILON) return -1;
        if (Math.abs(f - b) < Constants.EPSILON) return 1;
        if (f < a) return -2;
        if (f > b) return 2;
        return 0;
    }
}
