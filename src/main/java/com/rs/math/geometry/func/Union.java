package com.rs.math.geometry.func;

import com.rs.math.geometry.shape.MultiPolygon;
import com.rs.math.geometry.shape.Point;
import com.rs.math.geometry.shape.Polygon;
import com.rs.math.geometry.shape.Segment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Union {
    public static MultiPolygon unionWithoutHoles(Polygon polygon, MultiPolygon group) {
        //prepare segments
        List<Segment> s1l = new ArrayList<>(polygon.segments);
        List<List<Segment>> s2ll = new ArrayList<>();
        for (Polygon p2 : group.polygons) {
            s2ll.add(new ArrayList<>(p2.segments));
        }

        //test segments, insert points
        for (int i1 = 0; i1 < s1l.size(); i1++) {

            for (List<Segment> s2l : s2ll) {
                for (int i2 = 0; i2 < s2l.size(); i2++) {
                    Segment s1 = s1l.get(i1); //necessary reload
                    Segment s2 = s2l.get(i2);

                    Collision.SegmentResult r = Collision.intersect(s1, s2);
                    if (r.resultType == Collision.SegmentResultType.INTERSECTED) {
                        Point hitPoint = r.point;

                        insert(s1l, i1, hitPoint);
                        insert(s2l, i2, hitPoint);
                    }

                }
            }

        }

        //save polygons for testing
        Polygon p1 = segmentToPolygon(s1l);
        List<Polygon> p2l = new ArrayList<>();
        for (List<Segment> s2l : s2ll) {
            p2l.add(segmentToPolygon(s2l));
        }

        //clear group segments in polygon
        for (List<Segment> s2l : s2ll) {
            for (Iterator<Segment> iterator = s2l.iterator(); iterator.hasNext(); ) {
                Segment s2 = iterator.next();
                if (Collision.in(s2, p1)) {
                    iterator.remove();
                }
            }
        }

        //clear polygon segments in group
        //using saved polygons "p2l"
        for (Iterator<Segment> iterator = s1l.iterator(); iterator.hasNext(); ) {
            Segment s1 = iterator.next();

            boolean in = false;
            for (Polygon p2 : p2l) {
                if (Collision.in(s1, p2)) {
                    in = true;
                    break;
                }
            }
            if (in) {
                iterator.remove();
            }
        }

        //add to graph
        Map<Point, List<Point>> graph = new HashMap<>();
        for (Segment s1 : s1l) {
            addMultiMap(graph, s1.a, s1.b);
            addMultiMap(graph, s1.b, s1.a);
        }
        for (List<Segment> s2l : s2ll) {
            for (Segment s2 : s2l) {
                addMultiMap(graph, s2.a, s2.b);
                addMultiMap(graph, s2.b, s2.a);
            }
        }

        //basic checking
        for (Map.Entry<Point, List<Point>> e : graph.entrySet()) {
            if (e.getValue().size() != 2) {
                System.out.println("fail");
            }
        }

        //grab rings one by one
        List<Polygon> result = new ArrayList<>();
        while (!graph.isEmpty()) {
            List<Point> list = grabFromGraph(graph);
            result.add(new Polygon(list));
        }

        return new MultiPolygon(result);
    }

    private static <T> void addMultiMap(Map<T, List<T>> graph, T key, T value) {
        List<T> l = graph.get(key);
        if (l == null) graph.put(key, l = new ArrayList<>());
        l.add(value);
    }
    private static <T> List<T> grabFromGraph(Map<T, List<T>> graph) {
        List<T> r = new ArrayList<>();
        if (graph.isEmpty()) return r;
        T current = graph.keySet().iterator().next();

        for (; ; ) {
            r.add(current);
            List<T> list = graph.get(current);
            graph.remove(current);
            T next = null;
            for (T t : list) {
                if (graph.containsKey(t)) {
                    next = t;
                    break;
                }
            }
            if (next == null) {
                return r;
            }
            current = next;
        }
    }

    private static Polygon segmentToPolygon(List<Segment> segments) {
        List<Point> points = new ArrayList<>();
        for (Segment segment : segments) {
            points.add(segment.a);
        }
        return new Polygon(points);
    }

    private static void insert(List<Segment> list, int i, Point t) {
        Segment s = list.get(i);
        Point a = s.a;
        Point b = s.b;
        list.set(i, new Segment(a, t));
        list.add(i + 1, new Segment(t, b));
    }
}
