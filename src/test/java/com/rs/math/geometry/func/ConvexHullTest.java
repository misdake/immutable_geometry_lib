package com.rs.math.geometry.func;

import com.rs.math.geometry.shape.Point;
import com.rs.math.geometry.shape.Polygon;
import org.junit.Test;

import java.util.List;

public class ConvexHullTest {

    @Test
    public void simpleConvexHull_AndrewMonotoneChain() throws Exception {
        testConvexHull_AndrewMonotoneChain(new double[]{1, 1, 0, 0, 2, 2, 2, 0, 0, 2}, new double[]{0, 0, 0, 2, 2, 2, 2, 0});
        testConvexHull_AndrewMonotoneChain(new double[]{1, 1, 0, 0, 3, 1, 2, 2, 2, 0, 0, 2}, new double[]{0, 0, 0, 2, 2, 2, 3, 1, 2, 0});
    }

    @Test
    public void complexConvexHull_AndrewMonotoneChain() throws Exception {
        testConvexHull_AndrewMonotoneChain(new double[]{1, 1, 0, 0, 2, 2, 2, 0, 0, 2, 1, 2.00001f}, new double[]{0, 0, 0, 2, 2, 2, 2, 0});
        testConvexHull_AndrewMonotoneChain(new double[]{0, 0, 1, 0, 0, 1, 1 / 3f, 2 / 3f, 2 / 3f, 1 / 3f}, new double[]{0, 0, 1, 0, 0, 1});
    }

    private void testConvexHull_AndrewMonotoneChain(double[] in, double[] out) {
        List<Point> points = new Polygon(in).points;
        Polygon polygon = ConvexHull.convexHull_AndrewMonotoneChain(points);
        List<Point> ref = new Polygon(out).points;
        TestUtil.polygonEqual(polygon.points, ref);
    }


    @Test
    public void simpleConvexHull_misdake() throws Exception {
        testConvexHull_misdake(new double[]{1, 1, 0, 0, 2, 2, 2, 0, 0, 2}, new double[]{0, 0, 0, 2, 2, 2, 2, 0});
        testConvexHull_misdake(new double[]{1, 1, 0, 0, 3, 1, 2, 2, 2, 0, 0, 2}, new double[]{0, 0, 0, 2, 2, 2, 3, 1, 2, 0});
    }

    @Test
    public void complexConvexHull_misdake() throws Exception {
        testConvexHull_misdake(new double[]{1, 1, 0, 0, 2, 2, 2, 0, 0, 2, 1, 2.00001f}, new double[]{0, 0, 0, 2, 2, 2, 2, 0});
        testConvexHull_misdake(new double[]{0, 0, 1, 0, 0, 1, 1 / 3f, 2 / 3f, 2 / 3f, 1 / 3f}, new double[]{0, 0, 1, 0, 0, 1});
    }

    private void testConvexHull_misdake(double[] in, double[] out) {
        List<Point> points = new Polygon(in).points;
        Polygon polygon = ConvexHull.convexHull_misdake(points);
        List<Point> ref = new Polygon(out).points;
        TestUtil.polygonEqual(polygon.points, ref);
    }

}