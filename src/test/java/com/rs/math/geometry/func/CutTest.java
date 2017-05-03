package com.rs.math.geometry.func;

import com.rs.math.geometry.shape.Graph;
import com.rs.math.geometry.shape.Point;
import com.rs.math.geometry.shape.Polygon;
import com.rs.math.geometry.shape.Segment;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class CutTest {
    @Test
    public void testBorder() {
        Point p1 = new Point(0, 0);
        Point p2 = new Point(0, 4);
        Point p3 = new Point(4, 4);
        Point p4 = new Point(4, 0);
        Point p5 = new Point(3, 0);
        Point p6 = new Point(3, 3);
        Point p7 = new Point(1, 3);
        Point p8 = new Point(2, 2);

        Segment s1 = new Segment(p1, p2);
        Segment s2 = new Segment(p2, p3);
        Segment s3 = new Segment(p3, p4);
        Segment s4 = new Segment(p4, p5);
        Segment s5 = new Segment(p5, p6);
        Segment s6 = new Segment(p6, p7);
        Segment s7 = new Segment(p7, p1);
        Segment s8 = new Segment(p7, p8);

        Graph graph = new Graph(Arrays.asList(s1, s2, s3, s4, s5, s6, s7, s8));
        Polygon border = Cut.border(graph);

        assertNotNull(border);
    }
}
