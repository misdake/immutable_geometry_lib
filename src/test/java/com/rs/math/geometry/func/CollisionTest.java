package com.rs.math.geometry.func;

import com.rs.math.geometry.shape.*;
import org.junit.Test;

import static com.rs.math.geometry.func.Collision.SegmentResultType.*;
import static org.junit.Assert.*;

public class CollisionTest {

    @Test
    public void polygonTestPolygon() {
        {
            Polygon a = new Rect(new Point(0, 0), 2, 2, 0);
            Polygon b = new Rect(new Point(2, 0), 2, 2, 0);
            assertEquals(false, Collision.test(a,b));
        }
    }

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
            Polygon g = new Rect(new Point(0, 0), 2.8f, 2.8f, Math.toRadians(45));
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
            Segment l1 = new Segment(new Point(1, 0), new Point(0, 1));
            Segment l2 = new Segment(new Point(1, 0), new Point(0, 1));
            assertEquals(true, Collision.test(l1, l2));
        }
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
            Collision.SegmentResult r = Collision.intersect(l1, l2);
            assertEquals(INTERSECTED, r.resultType);
            assertEquals(new Point(0.5f, 0.5f), r.point);
        }
        {
            Line l1 = new Line(new Point(0, 0), new Point(0.2f, 0.2f));
            Line l2 = new Line(new Point(1, 0), new Point(2, 1));
            Collision.SegmentResult  r = Collision.intersect(l1, l2);
            assertEquals(NONE, r.resultType);
            assertEquals(null, r.point);
        }
        {
            Line l1 = new Line(new Point(0, 0), new Point(0.2f, 0.2f));
            Line l2 = new Line(new Point(0.5f, 0.5f), new Point(1, 1));
            Collision.SegmentResult r = Collision.intersect(l1, l2);
            assertEquals(SAME, r.resultType);
            assertEquals(null, r.point);
        }
    }

    @Test
    public void segmentIntersectLine() {
        {
            Segment l1 = new Segment(new Point(0, 0), new Point(0.2f, 0.2f));
            Line l2 = new Line(new Point(1, 0), new Point(0, 1));
            Collision.SegmentResult r = Collision.intersect(l1, l2);
            assertEquals(NONE, r.resultType);
            assertEquals(null, r.point);
        }
        {
            Segment l1 = new Segment(new Point(0, 0), new Point(1, 1));
            Line l2 = new Line(new Point(1, 0), new Point(0, 1));
            Collision.SegmentResult r = Collision.intersect(l1, l2);
            assertEquals(INTERSECTED, r.resultType);
            assertEquals(new Point(0.5f, 0.5f), r.point);
        }
        {
            Segment l1 = new Segment(new Point(0, 0), new Point(0.5f, 0.5f));
            Line l2 = new Line(new Point(1, 0), new Point(0, 1));
            Collision.SegmentResult r = Collision.intersect(l1, l2);
            assertEquals(CONNECTED, r.resultType);
            assertEquals(new Point(0.5f, 0.5f), r.point);
        }
        {
            Segment l1 = new Segment(new Point(0, 0), new Point(0.2f, 0.2f));
            Line l2 = new Line(new Point(1, 0), new Point(2, 1));
            Collision.SegmentResult r = Collision.intersect(l1, l2);
            assertEquals(NONE, r.resultType);
            assertEquals(null, r.point);
        }
        {
            Segment l1 = new Segment(new Point(0, 0), new Point(0.2f, 0.2f));
            Line l2 = new Line(new Point(0.5f, 0.5f), new Point(1, 1));
            Collision.SegmentResult r = Collision.intersect(l1, l2);
            assertEquals(IN, r.resultType);
            assertEquals(null, r.point);
        }
    }

    @Test
    public void segmentIntersectSegment() {
        {
            Segment l1 = new Segment(new Point(0, 0), new Point(1, 1));
            Segment l2 = new Segment(new Point(1, 1), new Point(0, 0));
            Collision.SegmentResult r = Collision.intersect(l1, l2);
            assertEquals(SAME, r.resultType);
            assertEquals(null, r.point);
        }
        {
            Segment l1 = new Segment(new Point(0, 0), new Point(0.2f, 0.2f));
            Segment l2 = new Segment(new Point(1, 0), new Point(0, 1));
            Collision.SegmentResult r = Collision.intersect(l1, l2);
            assertEquals(NONE, r.resultType);
            assertEquals(null, r.point);
        }
        {
            Segment l1 = new Segment(new Point(0, 0), new Point(1, 1));
            Segment l2 = new Segment(new Point(1, 0), new Point(0, 1));
            Collision.SegmentResult r = Collision.intersect(l1, l2);
            assertEquals(INTERSECTED, r.resultType);
            assertEquals(new Point(0.5f, 0.5f), r.point);
        }
        {
            Segment l1 = new Segment(new Point(0, 0), new Point(0.2f, 0.2f));
            Segment l2 = new Segment(new Point(1, 0), new Point(2, 1));
            Collision.SegmentResult r = Collision.intersect(l1, l2);
            assertEquals(NONE, r.resultType);
            assertEquals(null, r.point);
        }
        {
            Segment l1 = new Segment(new Point(0, 0), new Point(0.6f, 0.6f));
            Segment l2 = new Segment(new Point(0.5f, 0.5f), new Point(1, 1));
            Collision.SegmentResult r = Collision.intersect(l1, l2);
            assertEquals(INTERLEAVED, r.resultType);
            assertEquals(null, r.point);
        }
        {
            Segment l1 = new Segment(new Point(0, 0), new Point(0.2f, 0.2f));
            Segment l2 = new Segment(new Point(0.5f, 0.5f), new Point(1, 1));
            Collision.SegmentResult r = Collision.intersect(l1, l2);
            assertEquals(NONE, r.resultType);
            assertEquals(null, r.point);
        }

        {
            Segment l1 = new Segment(new Point(0, 0), new Point(2, 0));
            Segment l2 = new Segment(new Point(1, 0), new Point(2, 0));
            assertEquals(OUT_CONNECTED, Collision.intersect(l1, l2).resultType);
            assertEquals(IN_CONNECTED, Collision.intersect(l2, l1).resultType);
        }
        {
            Segment l1 = new Segment(new Point(0, 0), new Point(3, 0));
            Segment l2 = new Segment(new Point(1, 0), new Point(2, 0));
            assertEquals(OUT, Collision.intersect(l1, l2).resultType);
            assertEquals(IN, Collision.intersect(l2, l1).resultType);
        }

        {
            Segment l1 = new Segment(new Point(0, 0), new Point(1, 1));
            Segment l2 = new Segment(new Point(1, 1), new Point(2, 2));
            Collision.SegmentResult r = Collision.intersect(l1, l2);
            assertEquals(CONNECTED, r.resultType);
            assertTrue(Collision.is(r.point, l1.b));
            assertTrue(Collision.is(r.point, l2.a));
        }
        {
            Segment l1 = new Segment(new Point(0, 0), new Point(1, 1));
            Segment l2 = new Segment(new Point(1, 1), new Point(2, 0));
            Collision.SegmentResult r = Collision.intersect(l1, l2);
            assertEquals(CONNECTED, r.resultType);
            assertTrue(Collision.is(r.point, new Point(1, 1)));
        }
    }

    @Test
    public void pointInsidePolygon() {
        {
            Polygon g = new Rect(new Point(0, 0), 2, 2, 0);
            Point p1 = new Point(0.9, 0);
            Point p2 = new Point(1.1, 0);
            assertEquals(true, Collision.in(p1, g));
            assertEquals(false, Collision.in(p2, g));
        }
        {
            Polygon g = new Rect(new Point(123, 456), 2, 2, 0);
            Point p1 = new Point(0.9 + 123, 456);
            Point p2 = new Point(1.1 + 123, 456);
            assertEquals(true, Collision.in(p1, g));
            assertEquals(false, Collision.in(p2, g));
        }
        {
            Polygon g = new Rect(new Point(0, 0), 2.8f, 2.8, Math.toRadians(45));
            Point p1 = new Point(0.9, 0.9);
            Point p2 = new Point(1, 1);
            assertEquals(true, Collision.in(p1, g));
            assertEquals(false, Collision.in(p2, g));
        }
    }

}