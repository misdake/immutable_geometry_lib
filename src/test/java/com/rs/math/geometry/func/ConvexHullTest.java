package com.rs.math.geometry.func;

import com.rs.math.geometry.shape.Point;
import com.rs.math.geometry.shape.Polygon;
import org.junit.Test;

import java.util.List;

public class ConvexHullTest {

    @Test
    public void convexHull_JarvisMarch() throws Exception {
        testConvexHull(new float[]{1, 1, 0, 0, 2, 2, 2, 0, 0, 2}, new float[]{0, 0, 0, 2, 2, 2, 2, 0});
    }
    private void testConvexHull(float[] in, float[] out) {
        List<Point> points = new Polygon(in).points;
        Polygon polygon = ConvexHull.convexHull_JarvisMarch(points);
        List<Point> ref = new Polygon(out).points;
        TestUtil.polygonEqual(polygon.points, ref);
    }

}