package com.rs.math.geometry.func;

import com.rs.math.geometry.shape.MultiPolygon;
import com.rs.math.geometry.shape.Point;
import com.rs.math.geometry.shape.Polygon;
import com.rs.math.geometry.shape.Segment;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class Union {
    public static MultiPolygon unionWithoutHoles(Collection<Polygon> polygons) {
        //prepare segments
        Set<Point> allPoints = new HashSet<>();
        Map<Point, Point> pointMap = new HashMap<>();
        //add points to allPoints
        for (Polygon polygon : polygons) {
            for (Point a : polygon.points) {
                Point to = getCachedPoint(allPoints, a);
                pointMap.put(a, to);
            }
        }
        //add segments to allSegments
        List<Segment> allSegments = new ArrayList<>();
        for (Polygon polygon : polygons) {
            allSegments.addAll(polygon.segments);
        }

        Set<Segment> segments = unionSegments(allSegments);

        //TODO reconstruct graph
        //TODO while graph not empty
        //TODO     gather sub-graph
        //TODO     remove sub-graph from graph
        //TODO     generate outline of sub-graph using customized ConvexHull
        //TODO     add outline to result set

        return null;
    }

    public static Set<Segment> unionSegments(Collection<Segment> allSegments) {
        //prepare segments
        Set<Point> allPoints = new HashSet<>();
        Map<Point, Point> pointMap = new HashMap<>();
        //add points to allPoints
        for (Segment segment : allSegments) {
            Point a = getCachedPoint(allPoints, segment.a);
            Point b = getCachedPoint(allPoints, segment.b);
            pointMap.put(segment.a, a);
            pointMap.put(segment.b, b);
        }
        //add segments to allSegments
        Queue<Segment> segmentsToAdd = new ArrayDeque<>();
        for (Segment segment : allSegments) {
            segmentsToAdd.add(new Segment(pointMap.get(segment.a), pointMap.get(segment.b)));
        }

        LinkedList<Segment> currentSegments = new LinkedList<>();
        while (!segmentsToAdd.isEmpty()) {
            List<Segment> newSegments = new ArrayList<>();
            newSegments.add(segmentsToAdd.poll());
            for (ListIterator<Segment> testIter = currentSegments.listIterator(); testIter.hasNext(); ) {
                Segment seg = testIter.next();

                for (int i = 0; i < newSegments.size(); i++) {
                    Segment newSegment = newSegments.get(i);
                    Collision.SegmentResult r = Collision.intersect(newSegment, seg);
                    boolean toBreak = false;
                    switch (r.resultType) {
                        case INTERSECTED: {
                            //cut each in 2
                            Point point = getCachedPoint(allPoints, r.point);
                            //replace seg with two segments and skip them.
                            testIter.previous();
                            testIter.remove();
                            toBreak = true;
                            if (!Collision.is(seg.a, point)) testIter.add(new Segment(seg.a, point));
                            if (!Collision.is(point, seg.b)) testIter.add(new Segment(point, seg.b));
                            //replace newSegment with two segments and continue with the first one (then the second one).
                            if (!Collision.is(newSegment.a, point)) newSegments.set(i, new Segment(newSegment.a, point));
                            if (!Collision.is(point, newSegment.b)) newSegments.add(i + 1, new Segment(point, newSegment.b));
                            break;
                        }
                        case INTERLEAVED: {
                            //cut each in 2, replace newSegment with the non-common part.
                            int aa_index = r.indices[0];
                            int ab_index = r.indices[1];
                            int ba_index = r.indices[2];
                            int bb_index = r.indices[3];
                            if (aa_index == 1 || aa_index == 2) { //aa is in b
                                testIter.previous();
                                testIter.remove();
                                toBreak = true;
                                testIter.add(new Segment(seg.a, newSegment.a));
                                testIter.add(new Segment(newSegment.a, seg.b));
                                testIter.previous(); //don't skip. more in/out/interleaved may be produced.
                                testIter.previous();
                                if (Math.abs(ba_index - ab_index) < Math.abs(bb_index - ab_index)) {
                                    newSegments.set(i, new Segment(newSegment.b, seg.a)); //newSegment.b is closer to seg.a than seg.b
                                } else {
                                    newSegments.set(i, new Segment(newSegment.b, seg.b)); //newSegment.b is closer to seg.a than seg.b
                                }
                            } else { //ab is in b
                                testIter.previous();
                                testIter.remove();
                                toBreak = true;
                                testIter.add(new Segment(seg.a, newSegment.b));
                                testIter.add(new Segment(newSegment.b, seg.b));
                                testIter.previous(); //don't skip. more in/out/interleaved may be produced.
                                testIter.previous();
                                if (Math.abs(ba_index - aa_index) < Math.abs(bb_index - aa_index)) {
                                    newSegments.set(i, new Segment(newSegment.a, seg.a)); //newSegment.a is closer to seg.a than seg.b, replace
                                } else {
                                    newSegments.set(i, new Segment(newSegment.a, seg.b)); //newSegment.a is closer to seg.a than seg.b, replace
                                }
                                i--;
                            }
                            break;
                        }
                        case IN: {
                            //cut seg in 3, and remove newSegment
                            int aa_index = r.indices[0];
                            int ab_index = r.indices[1];
                            int ba_index = r.indices[2];
                            int bb_index = r.indices[3];
                            Point a1 = (aa_index < ab_index) ? newSegment.a : newSegment.b;
                            Point a2 = (aa_index < ab_index) ? newSegment.b : newSegment.a;
                            Point b1 = (ba_index < bb_index) ? seg.a : seg.b;
                            Point b2 = (ba_index < bb_index) ? seg.b : seg.a;
                            testIter.previous();
                            testIter.remove();
                            toBreak = true;
                            testIter.add(new Segment(b1, a1));
                            testIter.add(new Segment(a1, a2));
                            testIter.add(new Segment(a2, b2));
                            testIter.previous();
                            testIter.previous();
                            testIter.previous();
                            newSegments.remove(i);
                            i--;
                            break;
                        }
                        case IN_CONNECTED: {
                            //cut seg in 2, and remove newSegment
                            int aa_index = r.indices[0];
                            int ab_index = r.indices[1];
                            int ba_index = r.indices[2];
                            int bb_index = r.indices[3];
                            Point a1 = (aa_index < ab_index) ? newSegment.a : newSegment.b;
                            Point a2 = (aa_index < ab_index) ? newSegment.b : newSegment.a;
                            Point b1 = (ba_index < bb_index) ? seg.a : seg.b;
                            Point b2 = (ba_index < bb_index) ? seg.b : seg.a;
                            testIter.previous();
                            testIter.remove();
                            toBreak = true;
                            if (Collision.is(a1, b1)) {
                                testIter.add(new Segment(a1, a2));
                                testIter.add(new Segment(a2, b2));
                            } else if (Collision.is(a2, b2)) {
                                testIter.add(new Segment(b1, a1));
                                testIter.add(new Segment(a1, a2));
                            } else {
                                throw new RuntimeException();
                            }
                            testIter.previous();
                            testIter.previous();
                            newSegments.remove(i);
                            i--;
                            break;
                        }
                        case OUT: {
                            //replace newSegment with the two non-common parts
                            int aa_index = r.indices[0];
                            int ab_index = r.indices[1];
                            int ba_index = r.indices[2];
                            int bb_index = r.indices[3];
                            Point a1 = (aa_index < ab_index) ? newSegment.a : newSegment.b;
                            Point a2 = (aa_index < ab_index) ? newSegment.b : newSegment.a;
                            Point b1 = (ba_index < bb_index) ? seg.a : seg.b;
                            Point b2 = (ba_index < bb_index) ? seg.b : seg.a;
                            newSegments.remove(i);
                            i--;
                            newSegments.add(new Segment(a1, b1));
                            newSegments.add(new Segment(b2, a2));
                            break;
                        }
                        case OUT_CONNECTED:
                            //replace newSegment with the non-common part
                            int aa_index = r.indices[0];
                            int ab_index = r.indices[1];
                            int ba_index = r.indices[2];
                            int bb_index = r.indices[3];
                            Point a1 = (aa_index < ab_index) ? newSegment.a : newSegment.b;
                            Point a2 = (aa_index < ab_index) ? newSegment.b : newSegment.a;
                            Point b1 = (ba_index < bb_index) ? seg.a : seg.b;
                            Point b2 = (ba_index < bb_index) ? seg.b : seg.a;
                            if (Collision.is(a1, b1)) {
                                newSegments.set(i, new Segment(b2, a2));
                            } else if (Collision.is(a2, b2)) {
                                newSegments.set(i, new Segment(a1, b1));
                            } else {
                                throw new RuntimeException();
                            }
                            i--;
                            break;
                        case SAME:
                            newSegments.remove(i);
                            i--;
                            break;
                        case CONNECTED:
                            break;
                        case NONE:
                            break;
                    }
                    if (toBreak) {
                        break;
                    }
                }
            } //new segment tested
            currentSegments.addAll(newSegments);
        }

        return new HashSet<>(currentSegments);
    }

    private static Point getCachedPoint(Set<Point> allPoints, Point a) {
        if (allPoints.contains(a)) return a;

        Point to = a;
        for (Point b : allPoints) {
            if (Collision.is(a, b)) {
                to = b;
                break;
            }
        }
        allPoints.add(to);
        return to;
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
