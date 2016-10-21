package com.rs.math.geometry.func;

import com.rs.math.geometry.shape.Line;
import com.rs.math.geometry.shape.Point;
import com.rs.math.geometry.shape.Polygon;
import com.rs.math.geometry.shape.Rect;
import com.rs.math.geometry.shape.Segment;
import org.junit.Test;

import static com.rs.math.geometry.func.Collision.ResultType.IN;
import static com.rs.math.geometry.func.Collision.ResultType.ON;
import static com.rs.math.geometry.func.Collision.ResultType.OUT;
import static org.junit.Assert.*;

public class CollisionTest {

    @Test
    public void segmentInPolygon() {
        {
            Polygon g = new Rect(new Point(0, 0), 2, 2, 0);
            Point p1 = new Point(0.8f, 0);
            Point p2 = new Point(0.9f, 0);
            Point p3 = new Point(1.1f, 0);
            Point p4 = new Point(1.2f, 0);
            assertEquals(true, Collision.in(new Segment(p1, p2), g));
            assertEquals(false, Collision.in(new Segment(p2, p3), g));
            assertEquals(false, Collision.in(new Segment(p3, p4), g));
        }
        {
            Polygon g = new Rect(new Point(123, 456), 2, 2, 0);
            Point p1 = new Point(0.8f + 123, 456);
            Point p2 = new Point(0.9f + 123, 456);
            Point p3 = new Point(1.1f + 123, 456);
            Point p4 = new Point(1.2f + 123, 456);
            assertEquals(true, Collision.in(new Segment(p1, p2), g));
            assertEquals(false, Collision.in(new Segment(p2, p3), g));
            assertEquals(false, Collision.in(new Segment(p3, p4), g));
        }
        {
            Polygon g = new Rect(new Point(0, 0), 2.8f, 2.8f, (float) Math.toRadians(45));
            Point p1 = new Point(0.8f, 0.8f);
            Point p2 = new Point(0.9f, 0.9f);
            Point p3 = new Point(1, 1);
            Point p4 = new Point(1.1f, 1.1f);
            assertEquals(true, Collision.in(new Segment(p1, p2), g));
            assertEquals(false, Collision.in(new Segment(p2, p3), g));
            assertEquals(false, Collision.in(new Segment(p3, p4), g));
        }
    }

    @Test
    public void segmentTestSegment() {
        {
            Segment l1 = new Segment(new Point(0, 0), new Point(0.2f, 0.2f));
            Segment l2 = new Segment(new Point(1, 0), new Point(0, 1));
            assertEquals(false, Collision.test(l1, l2));
        }
        {
            Segment l1 = new Segment(new Point(0, 0), new Point(1, 1));
            Segment l2 = new Segment(new Point(1, 0), new Point(0, 1));
            assertEquals(true, Collision.test(l1, l2));
        }
        {
            Segment l1 = new Segment(new Point(0, 0), new Point(0.2f, 0.2f));
            Segment l2 = new Segment(new Point(1, 0), new Point(2, 1));
            assertEquals(false, Collision.test(l1, l2));
        }
        {
            Segment l1 = new Segment(new Point(0, 0), new Point(0.6f, 0.6f));
            Segment l2 = new Segment(new Point(0.5f, 0.5f), new Point(1, 1));
            assertEquals(true, Collision.test(l1, l2));
        }
        {
            Segment l1 = new Segment(new Point(0, 0), new Point(0.2f, 0.2f));
            Segment l2 = new Segment(new Point(0.5f, 0.5f), new Point(1, 1));
            assertEquals(false, Collision.test(l1, l2));
        }
    }

    @Test
    public void lineIntersectLine() {
        {
            Line l1 = new Line(new Point(0, 0), new Point(0.2f, 0.2f));
            Line l2 = new Line(new Point(1, 0), new Point(0, 1));
            Collision.Result r = Collision.intersect(l1, l2);
            assertEquals(IN, r.resultType);
            assertEquals(new Point(0.5f, 0.5f), r.hitPoint);
        }
        {
            Line l1 = new Line(new Point(0, 0), new Point(0.2f, 0.2f));
            Line l2 = new Line(new Point(1, 0), new Point(2, 1));
            Collision.Result r = Collision.intersect(l1, l2);
            assertEquals(OUT, r.resultType);
            assertEquals(null, r.hitPoint);
        }
        {
            Line l1 = new Line(new Point(0, 0), new Point(0.2f, 0.2f));
            Line l2 = new Line(new Point(0.5f, 0.5f), new Point(1, 1));
            Collision.Result r = Collision.intersect(l1, l2);
            assertEquals(ON, r.resultType);
            assertEquals(null, r.hitPoint);
        }
    }

    @Test
    public void segmentIntersectLine() {
        {
            Segment l1 = new Segment(new Point(0, 0), new Point(0.2f, 0.2f));
            Line l2 = new Line(new Point(1, 0), new Point(0, 1));
            Collision.Result r = Collision.intersect(l1, l2);
            assertEquals(OUT, r.resultType);
            assertEquals(null, r.hitPoint);
        }
        {
            Segment l1 = new Segment(new Point(0, 0), new Point(1, 1));
            Line l2 = new Line(new Point(1, 0), new Point(0, 1));
            Collision.Result r = Collision.intersect(l1, l2);
            assertEquals(IN, r.resultType);
            assertEquals(new Point(0.5f, 0.5f), r.hitPoint);
        }
        {
            Segment l1 = new Segment(new Point(0, 0), new Point(0.2f, 0.2f));
            Line l2 = new Line(new Point(1, 0), new Point(2, 1));
            Collision.Result r = Collision.intersect(l1, l2);
            assertEquals(OUT, r.resultType);
            assertEquals(null, r.hitPoint);
        }
        {
            Segment l1 = new Segment(new Point(0, 0), new Point(0.2f, 0.2f));
            Line l2 = new Line(new Point(0.5f, 0.5f), new Point(1, 1));
            Collision.Result r = Collision.intersect(l1, l2);
            assertEquals(ON, r.resultType);
            assertEquals(null, r.hitPoint);
        }
    }

    @Test
    public void segmentIntersectSegment() {
        {
            Segment l1 = new Segment(new Point(0, 0), new Point(0.2f, 0.2f));
            Segment l2 = new Segment(new Point(1, 0), new Point(0, 1));
            Collision.Result r = Collision.intersect(l1, l2);
            assertEquals(OUT, r.resultType);
            assertEquals(null, r.hitPoint);
        }
        {
            Segment l1 = new Segment(new Point(0, 0), new Point(1, 1));
            Segment l2 = new Segment(new Point(1, 0), new Point(0, 1));
            Collision.Result r = Collision.intersect(l1, l2);
            assertEquals(IN, r.resultType);
            assertEquals(new Point(0.5f, 0.5f), r.hitPoint);
        }
        {
            Segment l1 = new Segment(new Point(0, 0), new Point(0.2f, 0.2f));
            Segment l2 = new Segment(new Point(1, 0), new Point(2, 1));
            Collision.Result r = Collision.intersect(l1, l2);
            assertEquals(OUT, r.resultType);
            assertEquals(null, r.hitPoint);
        }
        {
            Segment l1 = new Segment(new Point(0, 0), new Point(0.6f, 0.6f));
            Segment l2 = new Segment(new Point(0.5f, 0.5f), new Point(1, 1));
            Collision.Result r = Collision.intersect(l1, l2);
            assertEquals(ON, r.resultType);
            assertEquals(null, r.hitPoint);
        }

        //TODO add connected result type. test using (0,0)-(1,1) X (1,1)-(2,2).

        {
            Segment l1 = new Segment(new Point(0, 0), new Point(0.2f, 0.2f));
            Segment l2 = new Segment(new Point(0.5f, 0.5f), new Point(1, 1));
            Collision.Result r = Collision.intersect(l1, l2);
            assertEquals(OUT, r.resultType);
            assertEquals(null, r.hitPoint);
        }
    }

    @Test
    public void pointInsidePolygon() {
        {
            Polygon g = new Rect(new Point(0, 0), 2, 2, 0);
            Point p1 = new Point(0.9f, 0);
            Point p2 = new Point(1.1f, 0);
            assertEquals(true, Collision.in(p1, g));
            assertEquals(false, Collision.in(p2, g));
        }
        {
            Polygon g = new Rect(new Point(123, 456), 2, 2, 0);
            Point p1 = new Point(0.9f + 123, 456);
            Point p2 = new Point(1.1f + 123, 456);
            assertEquals(true, Collision.in(p1, g));
            assertEquals(false, Collision.in(p2, g));
        }
        {
            Polygon g = new Rect(new Point(0, 0), 2.8f, 2.8f, (float) Math.toRadians(45));
            Point p1 = new Point(0.9f, 0.9f);
            Point p2 = new Point(1, 1);
            assertEquals(true, Collision.in(p1, g));
            assertEquals(false, Collision.in(p2, g));
        }
    }

}