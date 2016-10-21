package com.rs.math.geometry.shape;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Polygon {
    public final List<Point> points;
    public Polygon(List<Point> points) {
        this(points, false);
    }
    Polygon(List<Point> points, boolean trust) {
        if (trust) {
            this.points = points;
        } else {
            this.points = Collections.unmodifiableList(new ArrayList<Point>(points));
        }
    }
}
