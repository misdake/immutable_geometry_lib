package com.rs.math.geometry.func;

import com.rs.math.geometry.shape.Point;
import com.rs.math.geometry.shape.Polygon;
import org.junit.Test;

import java.util.List;

public class ConvexHullTest {

    @Test
    public void simpleConvexHull_JarvisMarch() throws Exception {
        testConvexHull_JarvisMarch(new float[]{1, 1, 0, 0, 2, 2, 2, 0, 0, 2}, new float[]{0, 0, 0, 2, 2, 2, 2, 0});
        testConvexHull_JarvisMarch(new float[]{1, 1, 0, 0, 3, 1, 2, 2, 2, 0, 0, 2}, new float[]{0, 0, 0, 2, 2, 2, 3, 1, 2, 0});
    }

    @Test
    public void complexConvexHull_JarvisMarch() throws Exception {
        testConvexHull_JarvisMarch(new float[]{1, 1, 0, 0, 2, 2, 2, 0, 0, 2, 1, 2.00001f}, new float[]{0, 0, 0, 2, 2, 2, 2, 0});
        testConvexHull_JarvisMarch(new float[]{0, 0, 1, 0, 0, 1, 1 / 3f, 2 / 3f, 2 / 3f, 1 / 3f}, new float[]{0, 0, 1, 0, 0, 1});
    }

    private void testConvexHull_JarvisMarch(float[] in, float[] out) {
        List<Point> points = new Polygon(in).points;
        Polygon polygon = ConvexHull.convexHull_JarvisMarch(points);
        List<Point> ref = new Polygon(out).points;
        TestUtil.polygonEqual(polygon.points, ref);
    }

}