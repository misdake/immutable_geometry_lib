package com.rs.math.geometry.func;

import com.rs.math.geometry.Constants;
import com.rs.math.geometry.shape.MultiPolygon;
import com.rs.math.geometry.shape.Point;
import com.rs.math.geometry.shape.Polygon;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;

public class UnionTest {

    /*
     * ┌───┬───┐
     * │   │   │
     * ├───┼───┤
     * │   │   │
     * └───┴───┘
     *  (╯°Д°)╯︵ ┻━┻
     */

    /*
     *   ┌───┐
     * ┌─┼─┐ │ p2
     * │ └─┼─┘
     * └───┘
     *  p1
     */
    private static Polygon p1 = new Polygon(new float[]{0, 0, 2, 0, 2, 2, 0, 2});
    private static Polygon p2 = new Polygon(new float[]{1, 1, 3, 1, 3, 3, 1, 3});

    /*
     * ┌──┐
     * │ ┌┼┐ p4
     * │ └┼┘
     * └──┘
     * p3
     */
    private static Polygon p3 = new Polygon(new float[]{0, 0, 2, 0, 2, 3, 0, 3});
    private static Polygon p4 = new Polygon(new float[]{1, 1, 3, 1, 3, 2, 1, 2});

    /*
     *   ┌─┐
     * ┌─┼─┼─┐ p6
     * └─┼─┼─┘
     *   └─┘
     *   p5
     */
    private static Polygon p5 = new Polygon(new float[]{1, 0, 2, 0, 2, 3, 1, 3});
    private static Polygon p6 = new Polygon(new float[]{0, 1, 3, 1, 3, 2, 0, 2});

    /*
     * ┌───┬───┐
     * │   │   │
     * └───┴───┘
     *   p1  p8
     */
    private static Polygon p7 = new Polygon(new float[]{2, 0, 4, 0, 4, 2, 2, 2});

    /*
     * ┌─┬─┬─┐
     * │ │ │ │
     * └─┴─┴─┘
     *  p1  p8
     */
    private static Polygon p8 = new Polygon(new float[]{0, 0, 2, 0, 2, 2, 0, 2});

    @Test
    public void simplePolygonUnionMultiPolygon() {
        polygonUnionMultiPolygon(p1, p2, new float[]{0, 0, 2, 0, 2, 1, 3, 1, 3, 3, 1, 3, 1, 2, 0, 2});
        polygonUnionMultiPolygon(p3, p4, new float[]{0, 0, 2, 0, 2, 1, 3, 1, 3, 2, 2, 2, 2, 3, 0, 3});
        polygonUnionMultiPolygon(p5, p6, new float[]{1, 0, 2, 0, 2, 1, 3, 1, 3, 2, 2, 2, 2, 3, 1, 3, 1, 2, 0, 2, 0, 1, 1, 1});
    }

    @Test
    @Ignore
    public void complexPolygonUnionMultiPolygon() {
        polygonUnionMultiPolygon(p1, p1, new float[]{0, 0, 2, 0, 2, 2, 0, 2});
        polygonUnionMultiPolygon(p1, p7, new float[]{0, 0, 4, 0, 4, 2, 0, 2});
        polygonUnionMultiPolygon(p1, p7, new float[]{0, 0, 4, 0, 4, 2, 0, 2});
        polygonUnionMultiPolygon(p1, p8, new float[]{0, 0, 3, 0, 3, 2, 0, 2});
        polygonUnionMultiPolygon(p4, p6, new float[]{0, 1, 3, 1, 3, 2, 0, 2});
        polygonUnionMultiPolygon(p1, p5, new float[]{0, 0, 2, 0, 2, 3, 1, 3, 1, 2, 0, 2});
    }

    private static void polygonUnionMultiPolygon(Polygon a, Polygon b, float[] reference) {
        MultiPolygon r = Union.unionWithoutHoles(a, new MultiPolygon(b));
        assertNotNull(r);
        assertEquals(1, r.polygons.size());
        List<Point> points = r.polygons.get(0).points;

        Polygon referenceResult = new Polygon(reference);
        assertEquals(referenceResult.points.size(), points.size());

        List<Point> t = new ArrayList<>(points);
        for (Point point : referenceResult.points) {
            Point x = nearestPoint(point, t);
            assertNotNull(x);
            t.remove(x);
            assertTrue(Distance.distance(point, x) < Constants.EPSILON);
        }
    }

    private static Point nearestPoint(Point p, Collection<Point> points) {
        Point min = null;
        float minD = Float.MAX_VALUE;
        for (Point point : points) {
            float d = Distance.distance(p, point);
            if (d < minD) {
                minD = d;
                min = point;
            }
        }
        return min;
    }
}
