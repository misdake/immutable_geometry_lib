package com.rs.math.geometry.shape;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Polygon {
    public final List<Point>   points;
    public final List<Segment> segments;
    public Polygon(List<Point> points) {
        this(points, false);
    }
    public Polygon(Point... points) {
        this(Arrays.asList(points), true);
    }
    public Polygon(double[] xy) {
        this(xyxyToPoints(xy), true);
    }
    public Polygon(double[] x, double[] y) {
        this(xxyyToPoints(x, y), true);
    }
    Polygon(List<Point> points, boolean owning) {
        if (!owning) {
            points = new ArrayList<>(points);
        }
        this.points = Collections.unmodifiableList(new ArrayList<>(points));
        this.segments = Collections.unmodifiableList(createSegments(points));
    }

    private static List<Segment> createSegments(List<Point> points) {
        List<Segment> segments = new ArrayList<>();
        for (int i = 0; i < points.size(); i++) {
            Point p1 = points.get(i);
            Point p2 = points.get((i + 1) % points.size());
            segments.add(new Segment(p1, p2));
        }
        return segments;
    }

    private static List<Point> xyxyToPoints(double[] xy) {
        List<Point> r = new ArrayList<>();
        if (xy == null) {
            throw new InvalidParameterException("double array is null");
        } else if (xy.length % 2 != 0) {
            throw new InvalidParameterException("double array length is not product of 2");
        } else {
            for (int i = 0; i < xy.length; i += 2) {
                r.add(new Point(xy[i], xy[i + 1]));
            }
            return r;
        }
    }

    private static List<Point> xxyyToPoints(double[] x, double[] y) {
        List<Point> r = new ArrayList<>();
        if (x == null || y == null) {
            throw new InvalidParameterException("double array is null");
        } else if (x.length != y.length) {
            throw new InvalidParameterException("array lengths are not equal");
        } else {
            for (int i = 0; i < x.length; i++) {
                r.add(new Point(x[i], y[i]));
            }
            return r;
        }
    }
}
