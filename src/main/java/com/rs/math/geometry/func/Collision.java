package com.rs.math.geometry.func;

import com.rs.math.geometry.Constants;
import com.rs.math.geometry.shape.Circle;
import com.rs.math.geometry.shape.Line;
import com.rs.math.geometry.shape.Point;
import com.rs.math.geometry.shape.Polygon;
import com.rs.math.geometry.shape.Segment;
import com.rs.math.geometry.value.Vector;

import java.util.List;

import static com.rs.math.geometry.func.Collision.ResultType.OUT;

public class Collision {

    public enum ResultType {
        IN,
        ON,
        OUT
    }

    public static class Result {
        public final ResultType resultType;
        public final Point      hitPoint;
        public Result(ResultType resultType, Point hitPoint) {
            this.resultType = resultType;
            this.hitPoint = hitPoint;
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

    // on

    public static boolean on(Point p, Segment s) {
        float maxLength = s.length();
        Vector v = new Vector(s.a, p);
        Line line = s.getLine();
        float distance = Distance.distance(p, line);
        if (distance > Constants.EPSILON) return false;
        boolean inSegment = Vector.dot(v, line.direction) <= maxLength;
        return inSegment;
    }

    // intersect

    public static Result intersect(Line a, Line b) {
        Point aProj = Projection.project(a.point, b);
        Vector v = new Vector(a.point, aProj);

        float length = v.length();
        if (length < Constants.EPSILON) {
            // point a is on line b => ON
            return new Result(ResultType.ON, null);
        }

        float direction = Vector.dot(v, a.direction);
        if (Math.abs(direction) < Constants.EPSILON) {
            // projection (point a -> line b) direction is perpendicular to line a => parallel => OUT
            return new Result(OUT, null);
        }

        float t = length * length / direction;
        float x = a.point.x + a.direction.x * t;
        float y = a.point.y + a.direction.y * t;
        return new Result(ResultType.IN, new Point(x, y));
    }

    public static Result intersect(Segment a, Line b) {
        //LATER implement cross product shortcut
        Line line = a.getLine();
        Result r = intersect(line, b);
        switch (r.resultType) {
            case IN: // test
                boolean in = on(r.hitPoint, a);
                return in ? r : new Result(OUT, null);
            case ON:
            case OUT:
                return r;
        }
        throw new RuntimeException("unexpected resultType");
    }

    public static Result intersect(Segment a, Segment b) {
        //LATER implement cross product shortcut
        Line la = a.getLine();
        Line lb = b.getLine();
        Result r = intersect(la, lb);
        switch (r.resultType) {
            case IN: // test
                boolean inA = on(r.hitPoint, a);
                boolean inB = on(r.hitPoint, b);
                return inA && inB ? r : new Result(OUT, null);
            case ON:
                boolean onA = on(b.a, a);
                boolean onB = on(b.b, a);
                return onA || onB ? r : new Result(OUT, null);
            case OUT:
                return r;
        }
        throw new RuntimeException("unexpected resultType");
    }
}
