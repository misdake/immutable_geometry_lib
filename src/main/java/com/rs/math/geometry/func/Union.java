package com.rs.math.geometry.func;

import com.rs.math.geometry.Constants;
import com.rs.math.geometry.shape.MultiPolygon;
import com.rs.math.geometry.shape.Point;
import com.rs.math.geometry.shape.Polygon;
import com.rs.math.geometry.shape.Segment;
import com.rs.math.geometry.util.AABB;
import com.rs.math.geometry.util.QuadTree;
import com.rs.math.geometry.util.QuadTree2;

import java.util.*;

public class Union {
    public static MultiPolygon unionWithoutHoles(Collection<Polygon> polygons) {
        AABB aabb = new AABB();
        //add segments to allSegments
        List<Segment> allSegments = new ArrayList<>();
        for (Polygon polygon : polygons) {
            for (Segment segment : polygon.segments) {
                if (segment.length() > 2 * Constants.EPSILON) {
                    allSegments.add(segment);
                }
                aabb.combine(segment.a.x, segment.a.y);
                aabb.combine(segment.b.x, segment.b.y);
            }
        }

        //generate plain segments
        Set<Segment> segments = unionSegments(allSegments);

        //reconstruct graph
        QuadTree<Point> allPoints = new QuadTree<>(aabb.getMinX(), aabb.getMaxX(), aabb.getMinY(), aabb.getMaxY());
        final Map<Point, List<Point>> graph = new HashMap<>();
        for (Segment segment : segments) {
            Point a = getCachedPoint(allPoints, segment.a);
            Point b = getCachedPoint(allPoints, segment.b);
        }
        for (Segment segment : segments) {
            Point a = getCachedPoint(allPoints, segment.a);
            Point b = getCachedPoint(allPoints, segment.b);
            addMultiMap(graph, a, b);
            addMultiMap(graph, b, a);
        }

        List<Polygon> r = new ArrayList<>();
        //extract sub-graph one by one
        while (!graph.isEmpty()) {

            //gather sub-graph
            Queue<Point> queue = new ArrayDeque<>();
            List<Point> points = new ArrayList<>();
            Point first = graph.keySet().iterator().next();
            queue.add(first);
            while (!queue.isEmpty()) {
                Point p = queue.poll();
                points.add(p);
                List<Point> list = graph.get(p);
                for (Point point : list) {
                    if (!queue.contains(point) && !points.contains(point)) {
                        queue.add(point);
                    }
                }
            }

            //generate outline of sub-graph using customized ConvexHull
            Polygon polygon = ConvexHull.convexHull_misdake(points, graph);
            Polygon refined = refine(polygon);
            r.add(refined);

            //remove sub-graph from graph
            for (Point point : points) {
                graph.remove(point);
            }
            //remove points inside
            List<Point> left = new ArrayList<>(graph.keySet());
            for (Point point : left) {
                if (Collision.in(point, polygon)) {
                    graph.remove(point);
                }
            }
        }

        return new MultiPolygon(r);
    }

    public static Polygon refine(Polygon input) {
        List<Point> points = input.points;
        List<Point> r = new ArrayList<>(points);
        int size = points.size();
        for (int i = 0; i < size; i++) {
            Point p0 = points.get((i + 0) % size);
            Point p1 = points.get((i + 1) % size);
            Point p2 = points.get((i + 2) % size);
            if(Collision.on(p1, new Segment(p0, p2))) {
                r.remove(p1);
            }
        }
        return new Polygon(r);
    }

    private static class SegmentRegion extends AABB {
        public final Segment segment;
        public SegmentRegion(Segment segment) {
            this.segment = segment;
            this.combine(segment.a.x, segment.a.y);
            this.combine(segment.b.x, segment.b.y);
        }
    }

    public static Set<Segment> unionSegments(Collection<Segment> allSegments) {
        AABB aabb = new AABB();
        for (Segment segment : allSegments) {
            aabb.combine(segment.a.x, segment.a.y);
            aabb.combine(segment.b.x, segment.b.y);
        }
        //prepare segments
        QuadTree<Point> allPoints = new QuadTree<>(aabb.getMinX(), aabb.getMaxX(), aabb.getMinY(), aabb.getMaxY());

        Queue<Segment> segmentsToAdd = new ArrayDeque<>();
        {
            Map<Point, Point> pointMap = new HashMap<>();
            //add points to allPoints
            for (Segment segment : allSegments) {
                Point a = getCachedPoint(allPoints, segment.a);
                Point b = getCachedPoint(allPoints, segment.b);
                pointMap.put(segment.a, a);
                pointMap.put(segment.b, b);
            }
            //add segments to allSegments
            for (Segment segment : allSegments) {
                segmentsToAdd.add(new Segment(pointMap.get(segment.a), pointMap.get(segment.b)));
            }
        }

        QuadTree2<SegmentRegion> regions = new QuadTree2<>(aabb.getMinX(), aabb.getMaxX(), aabb.getMinY(), aabb.getMaxY());

        Set<Segment> addedSegments = new HashSet<>();
        while (!segmentsToAdd.isEmpty()) {
            List<Segment> newSegments = new ArrayList<>();
            Segment oriNewSeg = segmentsToAdd.poll();
            newSegments.add(oriNewSeg);

            double minnx = Math.min(oriNewSeg.a.x, oriNewSeg.b.x) - Constants.EPSILON_STABLE;
            double minny = Math.min(oriNewSeg.a.y, oriNewSeg.b.y) - Constants.EPSILON_STABLE;
            double maxnx = Math.max(oriNewSeg.a.x, oriNewSeg.b.x) + Constants.EPSILON_STABLE;
            double maxny = Math.max(oriNewSeg.a.y, oriNewSeg.b.y) + Constants.EPSILON_STABLE;

            //gather possible segments to test
            ArrayList<SegmentRegion> found = regions.find(minnx, maxnx, minny, maxny);
            Set<Segment> possibleSegments = new HashSet<>();
            for (SegmentRegion region : found) {
                Segment seg = region.segment;
                if (!addedSegments.contains(seg)) continue;
                double mintx = Math.min(seg.a.x, seg.b.x) - Constants.EPSILON_STABLE;
                double minty = Math.min(seg.a.y, seg.b.y) - Constants.EPSILON_STABLE;
                double maxtx = Math.max(seg.a.x, seg.b.x) + Constants.EPSILON_STABLE;
                double maxty = Math.max(seg.a.y, seg.b.y) + Constants.EPSILON_STABLE;
                if (maxnx < mintx || maxny < minty || maxtx < minnx || maxty < minny) continue;
                possibleSegments.add(seg);
                //remove from current added
                addedSegments.remove(seg);
                regions.remove(region);
            }

            //test new segment with possible segments
            LinkedList<Segment> currentSegments = new LinkedList<>(possibleSegments);
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
                            testIter.previous();
                            testIter.remove();
                            toBreak = true;
                            if (!Collision.is(seg.a, point)) testIter.add(new Segment(seg.a, point));
                            if (!Collision.is(point, seg.b)) testIter.add(new Segment(point, seg.b));
                            newSegments.remove(i);
                            if (!Collision.is(newSegment.a, point)) newSegments.add(i, new Segment(newSegment.a, point));
                            if (!Collision.is(point, newSegment.b)) newSegments.add(i, new Segment(point, newSegment.b));
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
                            } else if (Distance.distance(a1, b1) < Constants.EPSILON * 2) {
                                newSegments.set(i, new Segment(b2, a2));
                            } else if (Distance.distance(a2, b2) < Constants.EPSILON * 2) {
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

            addedSegments.addAll(newSegments);
            addedSegments.addAll(currentSegments);
            for (Segment segment : newSegments) regions.insert(new SegmentRegion(segment));
            for (Segment segment : currentSegments) regions.insert(new SegmentRegion(segment));
        }

        return new HashSet<>(addedSegments);
    }

    private static Point getCachedPoint(QuadTree<Point> allPoints, Point a) {
        Point p = allPoints.nearest(a, Constants.EPSILON * 2);
        if (p != null) return p;
        allPoints.insert(a);
        return a;
    }

    private static <T> void addMultiMap(Map<T, List<T>> graph, T key, T value) {
        List<T> l = graph.get(key);
        if (l == null) graph.put(key, l = new ArrayList<>());
        if (!l.contains(value)) l.add(value);
    }
}
