package com.rs.math.geometry.shape;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MultiPolygon {
    public final List<Polygon> polygons;
    public MultiPolygon(Polygon... polygons) {
        this(Arrays.asList(polygons), true);
    }
    public MultiPolygon(Collection<Polygon> polygons) {
        this.polygons = Collections.unmodifiableList(new ArrayList<>(polygons));
    }

    MultiPolygon(List<Polygon> polygons, boolean unmodifiable) {
        if (unmodifiable) {
            this.polygons = polygons;
        } else {
            this.polygons = Collections.unmodifiableList(new ArrayList<>(polygons));
        }
    }
}
