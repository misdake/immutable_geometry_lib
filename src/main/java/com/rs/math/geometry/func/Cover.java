package com.rs.math.geometry.func;

import com.rs.math.geometry.shape.Circle;
import com.rs.math.geometry.shape.Point;

import java.util.*;

public class Cover {

    public static void main(String[] args) {
        Circle circle = minCircle(Arrays.asList(
                new Point(0, 1),
                new Point(0, 0),
                new Point(2, 0)
        ));
        System.out.printf("(%s,%s) %s\n", circle.center.x, circle.center.y, circle.radius);
    }

    public static Circle minCircle(Collection<Point> points) {
        List<Point> list = new ArrayList<>(points);
        Collections.shuffle(list);

        Circle r = new Circle(new Point(0, 0), 0);

        int n = list.size();
        for (int i = 0; i < n; i++) {
            Point pi = list.get(i);
            if (!Collision.in(pi, r)) {
                r = new Circle(new Point(pi.x, pi.y), 0);

                for (int j = 0; j < i; j++) {
                    Point pj = list.get(j);
                    if (!Collision.in(pj, r)) {
                        double x = (pi.x + pj.x) / 2;
                        double y = (pi.y + pj.y) / 2;
                        double radius = Distance.distance(pi, pj) / 2;
                        r = new Circle(new Point(x, y), radius);

                        for (int k = 0; k < j; k++) {
                            Point pk = list.get(k);
                            if (!Collision.in(pk, r)) {
                                Point center = solve(
                                        pi.x - pj.x, pi.y - pj.y, ((pj.x * pj.x + pj.y * pj.y) - (pi.x * pi.x + pi.y * pi.y)) / 2,
                                        pi.x - pk.x, pi.y - pk.y, ((pk.x * pk.x + pk.y * pk.y) - (pi.x * pi.x + pi.y * pi.y)) / 2
                                );
                                double d = Distance.distance(center, pi);
                                r = new Circle(center, d);
                            }
                        }

                    }
                }

            }
        }

        return r;
    }

    private static Point solve(double a, double b, double c, double d, double e, double f) {
        double y = (f * a - c * d) / (b * d - e * a);
        double x = (f * b - c * e) / (a * e - b * d);
        return new Point(x, y);
    }

}
