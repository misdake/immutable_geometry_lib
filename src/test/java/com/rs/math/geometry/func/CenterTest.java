package com.rs.math.geometry.func;

import com.rs.math.geometry.shape.Point;
import com.rs.math.geometry.shape.Polygon;
import org.junit.Test;

public class CenterTest {
    @Test
    public void visualCenter() throws Exception {
        Polygon polygon = new Polygon(new Point(39010.214009, 243079.544812),
                                      new Point(39010.213999, 243079.430165),
                                      new Point(39710.099353, 242379.430174),
                                      new Point(39710.099362, 243079.544821));
        Point point = Center.visualCenter(polygon, 1);
        System.out.println(point.x);
        System.out.println(point.y);
    }

}