package com.rs.math.geometry.func;

import com.rs.math.geometry.shape.Line;
import com.rs.math.geometry.shape.Point;
import com.rs.math.geometry.shape.Polygon;
import com.rs.math.geometry.shape.Rect;
import org.junit.jupiter.api.Test;

import static com.rs.math.geometry.func.Collision.ResultType.IN;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CollisionTest {

    @Test
    void lineIntersectLine() {
        {
            Line l1 = new Line(new Point(0, 0), new Point(0.2f, 0.2f));
            Line l2 = new Line(new Point(1, 0), new Point(0, 1));
            Collision.Result r = Collision.intersect(l1, l2);
            assertEquals(IN, r.resultType);
            assertEquals(new Point(0.5f, 0.5f), r.hitPoint);
        }

    }

    @Test
    void pointInsidePolygon() {
        {
            Polygon g = new Rect(new Point(0, 0), 2, 2, 0);
            Point p1 = new Point(0.9f, 0);
            Point p2 = new Point(1.1f, 0);
            assertEquals(true, Collision.inside(p1, g));
            assertEquals(false, Collision.inside(p2, g));
        }
        {
            Polygon g = new Rect(new Point(123, 456), 2, 2, 0);
            Point p1 = new Point(0.9f + 123, 456);
            Point p2 = new Point(1.1f + 123, 456);
            assertEquals(true, Collision.inside(p1, g));
            assertEquals(false, Collision.inside(p2, g));
        }
        {
            Polygon g = new Rect(new Point(0, 0), 2.8f, 2.8f, (float) Math.toRadians(45));
            Point p1 = new Point(0.9f, 0.9f);
            Point p2 = new Point(1, 1);
            assertEquals(true, Collision.inside(p1, g));
            assertEquals(false, Collision.inside(p2, g));
        }
    }

}