package com.rs.math.geometry.func;

import com.rs.math.geometry.shape.Point;
import com.rs.math.geometry.shape.Segment;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class CdtTest {

    private static Set<String> segmentStrings(Segment... e) {
        Set<String> result = new HashSet<>();
        for (Segment segment : e) {
            result.add(segment.toString());
            result.add(new Segment(segment.b, segment.a).toString());
        }
        return result;
    }

    private static void testResult1(Segment[] result, Segment[] yes, Segment[] no) {
        Set<String> r = segmentStrings(result);
        for (Segment segment : yes) {
            assertTrue(r.contains(segment.toString()));
        }
        for (Segment segment : no) {
            assertFalse(r.contains(segment.toString()));
        }
    }

    @Test
    public void run() throws Exception {
        Point[] points = {
                new Point(0, 0),
                new Point(2, 2),
                new Point(0.5, 1),
                new Point(1.5, 1),
                new Point(2, 0),
                new Point(0, 2),
        };

        Segment[] constraints = {new Segment(points[4], points[5])};
        Triangulation.CdtResult r = Triangulation.cdt(Arrays.asList(points), Arrays.asList(constraints));
        assertNotNull(r);
        List<Segment> segments = new ArrayList<>();
        segments.addAll(r.constraints);
        segments.addAll(r.newEdges);

        Segment[] yes = {
                new Segment(points[0], points[2]),
                new Segment(points[1], points[3]),
                new Segment(points[0], points[5]),
                new Segment(points[0], points[4]),
                new Segment(points[1], points[5]),
                new Segment(points[1], points[4]),
                new Segment(points[4], points[5]),
        };
        Segment[] no = {
                new Segment(points[2], points[3]),
                new Segment(points[0], points[1]),
                new Segment(points[1], points[2]),
                new Segment(points[0], points[3]),
        };
        testResult1(segments.toArray(new Segment[0]), yes, no);
    }

}