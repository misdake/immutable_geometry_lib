package com.rs.math.geometry.func;

import com.rs.math.geometry.shape.MultiPolygon;
import com.rs.math.geometry.shape.Point;
import com.rs.math.geometry.shape.Polygon;
import com.rs.math.geometry.shape.Segment;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
     *  p1   p7
     */
    private static Polygon p7   = new Polygon(new float[]{2, 0, 4, 0, 4, 2, 2, 2});
    private static Polygon p7_1 = new Polygon(new float[]{2.00001f, 0, 4.00001f, 0, 4.00001f, 2, 2.00001f, 2});

    /*
     * ┌─┬─┬─┐
     * │ │ │ │
     * └─┴─┴─┘
     *  p1 p8
     */
    private static Polygon p8 = new Polygon(new float[]{1, 0, 3, 0, 3, 2, 1, 2});

    @Test
    public void simpleSegmentUnion() {
        segmentUnion(p1, p2, 12);
        segmentUnion(p3, p4, 12);
        segmentUnion(p5, p6, 16);
        segmentUnion(p1, p7, 7);
        segmentUnion(p1, p7_1, 7);
        segmentUnion(p1, p8, 10);
    }
    private void segmentUnion(Polygon p1, Polygon p2, int expectedSegmentCount) {
        Set<Segment> allSegments = new HashSet<>();
        allSegments.addAll(p1.segments);
        allSegments.addAll(p2.segments);
        Set<Segment> segments = Union.unionSegments(allSegments);
        //TODO more checking
        assertEquals(expectedSegmentCount, segments.size());
    }

    @Test
    @Ignore
    public void simplePolygonUnionMultiPolygon() {
        polygonUnionPolygon(p1, p2, new float[]{0, 0, 2, 0, 2, 1, 3, 1, 3, 3, 1, 3, 1, 2, 0, 2});
        polygonUnionPolygon(p3, p4, new float[]{0, 0, 2, 0, 2, 1, 3, 1, 3, 2, 2, 2, 2, 3, 0, 3});
        polygonUnionPolygon(p5, p6, new float[]{1, 0, 2, 0, 2, 1, 3, 1, 3, 2, 2, 2, 2, 3, 1, 3, 1, 2, 0, 2, 0, 1, 1, 1});
    }

    @Test
    @Ignore
    public void complexPolygonUnionMultiPolygon() {
        polygonUnionPolygon(p1, p1, new float[]{0, 0, 2, 0, 2, 2, 0, 2});
        polygonUnionPolygon(p1, p7, new float[]{0, 0, 4, 0, 4, 2, 0, 2});
        polygonUnionPolygon(p1, p7_1, new float[]{0, 0, 4, 0, 4, 2, 0, 2});
        polygonUnionPolygon(p4, p6, new float[]{0, 1, 3, 1, 3, 2, 0, 2});
        polygonUnionPolygon(p1, p5, new float[]{0, 0, 2, 0, 2, 3, 1, 3, 1, 2, 0, 2});
        polygonUnionPolygon(p1, p8, new float[]{0, 0, 3, 0, 3, 2, 0, 2});
    }

    private static void polygonUnionPolygon(Polygon a, Polygon b, float[] reference) {
        MultiPolygon r = Union.unionWithoutHoles(Arrays.asList(a, b));
        assertNotNull(r);
        assertEquals(1, r.polygons.size());
        List<Point> points = r.polygons.get(0).points;

        Polygon referenceResult = new Polygon(reference);
        assertEquals(referenceResult.points.size(), points.size());

        TestUtil.polygonEqual(points, referenceResult.points);
    }

}
