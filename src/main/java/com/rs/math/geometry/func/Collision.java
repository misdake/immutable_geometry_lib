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
import static com.rs.math.geometry.func.Collision.SegmentResultType.INSIDE;
import static com.rs.math.geometry.func.Collision.SegmentResultType.INTERSECTED;
import static com.rs.math.geometry.func.Collision.SegmentResultType.NON;
import static com.rs.math.geometry.func.Collision.SegmentResultType.SAME;
import static com.rs.math.geometry.func.Collision.SegmentResultType.SHARED;

public class Collision {

    public enum SegmentResultType {
        SAME,
        INSIDE,
        SHARED,
        CONNECTED,
        INTERSECTED,
        NON,
    }

    public static class SegmentResult {
        public final SegmentResultType resultType;
        public final Point             point;
        public SegmentResult(SegmentResultType resultType, Point point) {
            this.resultType = resultType;
            this.point = point;
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
        boolean ta = Vector.cross(va, new Vector(a1, b1)) * Vector.cross(va, new Vector(a1, b2)) < Constants.EPSILON;
        boolean tb = Vector.cross(vb, new Vector(b1, a1)) * Vector.cross(vb, new Vector(b1, a2)) < Constants.EPSILON;
        return ta && tb;
    }

    public static boolean test(Segment a, Segment b) {
        SegmentResult r = intersect(a, b);
        return r.resultType != NON;
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

    public static boolean on(Point p, Polygon polygon) {
        for (Segment segment : polygon.segments) {
            if (on(p, segment)) return true;
        }
        return false;
    }

    // intersect

    public static SegmentResult intersect(Line a, Line b) { // SAME || INTERSECTED || NON
        Point aProj = Projection.project(a.point, b);
        Vector v = new Vector(a.point, aProj);

        float length = v.length();
        if (length < Constants.EPSILON) {
            // point a is on line b => SAME
            return new SegmentResult(SAME, null);
        }

        float direction = Vector.dot(v, a.direction);
        if (Math.abs(direction) < Constants.EPSILON) {
            // projection (point a -> line b) direction is perpendicular to line a => parallel => NON
            return new SegmentResult(NON, null);
        }

        float t = length * length / direction;
        float x = a.point.x + a.direction.x * t;
        float y = a.point.y + a.direction.y * t;
        return new SegmentResult(INTERSECTED, new Point(x, y));
    }

    public static SegmentResult intersect(Segment a, Line b) { // INSIDE || INTERSECTED || NON
        Line line = a.getLine();
        SegmentResult r = intersect(line, b);
        switch (r.resultType) { // SAME || INTERSECTED || NON
            case INTERSECTED: // test
                boolean intersected = on_trusted(r.point, a);
                return intersected ? r : new SegmentResult(NON, null);
            case SAME:
                return new SegmentResult(INSIDE, null);
            case NON:
                return r;
        }
        throw new RuntimeException("unexpected resultType");
    }

    public static SegmentResult intersect(Segment a, Segment b) {
        if (!testPossible(a, b)) {
            return new SegmentResult(NON, null);
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
                if (is_aa_ba || is_ab_bb || is_aa_bb || is_ab_ba) {
                    return new SegmentResult(CONNECTED, null);
                }

                //test point
                boolean inA = on_trusted(r.point, a);
                boolean inB = on_trusted(r.point, b);
                return inA && inB ? r : new SegmentResult(INTERSECTED, null);
            }

            case SAME: {
                //share two points
                boolean is_aa_ba = is(a.a, b.a);
                boolean is_ab_bb = is(a.b, b.b);
                boolean is_aa_bb = is(a.a, b.b);
                boolean is_ab_ba = is(a.b, b.a);
                boolean same = (is_aa_ba && is_ab_bb) || (is_aa_bb && is_ab_ba);
                if (same) return r;

                //share one point
                boolean on_ba_a = on_trusted(b.a, a);
                boolean on_bb_a = on_trusted(b.b, a);
                boolean on_aa_b = on_trusted(a.a, b);
                boolean on_ab_b = on_trusted(a.b, b);
                if (is_aa_ba) return (on_ab_b || on_bb_a) ? new SegmentResult(INSIDE, null) : new SegmentResult(CONNECTED, null);
                if (is_aa_bb) return (on_ab_b || on_ba_a) ? new SegmentResult(INSIDE, null) : new SegmentResult(CONNECTED, null);
                if (is_ab_ba) return (on_aa_b || on_bb_a) ? new SegmentResult(INSIDE, null) : new SegmentResult(CONNECTED, null);
                if (is_ab_bb) return (on_aa_b || on_ba_a) ? new SegmentResult(INSIDE, null) : new SegmentResult(CONNECTED, null);

                //no shared point
                if ((on_ba_a && on_bb_a) || (on_aa_b && on_ab_b)) return new SegmentResult(INSIDE, null);
                return !on_ba_a && !on_bb_a && !on_aa_b && !on_ab_b ? new SegmentResult(NON, null) : new SegmentResult(SHARED, null);
            }
            case NON:
                return r;
        }
        throw new RuntimeException("unexpected resultType");
    }
}
