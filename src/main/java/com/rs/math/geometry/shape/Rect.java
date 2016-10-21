package com.rs.math.geometry.shape;

import com.rs.math.geometry.func.Distance;
import com.rs.math.geometry.func.Interpolation;

import java.util.ArrayList;
import java.util.List;

import static com.rs.math.geometry.func.Rotation.rotate;

public class Rect extends Polygon {
    public final Point  center;
    public final float  width;
    public final float  height;
    public final float  rotation;

    public Rect(Point center, float width, float height, float rotation) {
        super(generatePoints(center, width, height, rotation), true);
        this.center = new Point(center);
        this.width = width;
        this.height = height;
        this.rotation = rotation;
    }
    public Rect(Point focus1, Point focus2, float radius, float rotation) {
        this(Interpolation.lerp(focus1, focus2, 0.5f),
             Distance.distance(focus1, focus2) + radius * 2,
             radius * 2,
             rotation);
    }

    private static List<Point> generatePoints(Point center, float width, float height, float rotation) {
        List<Point> points = new ArrayList<Point>();
        float x = width / 2;
        float y = height / 2;
        points.add(rotate(-x + center.x, -y + center.y, center, rotation));
        points.add(rotate(+x + center.x, -y + center.y, center, rotation));
        points.add(rotate(+x + center.x, +y + center.y, center, rotation));
        points.add(rotate(-x + center.x, +y + center.y, center, rotation));
        return points;
    }
}
