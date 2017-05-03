package com.rs.math.geometry.func;

import com.rs.math.geometry.shape.*;
import com.rs.math.geometry.util.UnionFind;

import java.util.*;

public class Cut {

    public static void main(String[] args) {
        Point[] p = new Point[]{
                new Point(200.0f, 143.0f),
                new Point(296.0f, 141.0f),
                new Point(215.0f, 233.0f),
                new Point(305.0f, 231.0f),
        };
        Segment[] e = new Segment[]{
                new Segment(p[1], p[3]),
                new Segment(p[0], p[2]),
                new Segment(p[0], p[1]),
                new Segment(p[1], p[2]),
                new Segment(p[2], p[3]),
        };
        Graph graph = new Graph(Arrays.asList(e)).validate();
        for (int i = 0; i != 10000; i++) {
            Point[] px = new Point[]{
                    new Point(0, 0),
                    new Point(1, 0),
                    new Point(2, 0),
                    new Point(0, 1),
                    new Point(1, 1),
                    new Point(0, 2),
                    new Point(2, 2),
            };
            Segment[] ex = new Segment[]{
                    new Segment(px[0], px[1]),
                    new Segment(px[1], px[2]),
                    new Segment(px[2], px[6]),
                    new Segment(px[6], px[5]),
                    new Segment(px[5], px[3]),
                    new Segment(px[3], px[0]),
                    new Segment(px[3], px[4]),
                    new Segment(px[4], px[1]),
                    new Segment(px[4], px[6]),
            };
            Graph graphx = new Graph(Arrays.asList(ex)).validate();
            MultiPolygon r = cutPlaneByPlanarGraph(graphx);
        }
//        System.out.println(r);
    }

    public static Set<Graph> connectedGraphs(Graph graph) {
        List<Point> vertices = new ArrayList<>(graph.vertices);
        Map<Point, Integer> indices = new HashMap<>();
        for (int i = 0; i < vertices.size(); i++) {
            indices.put(vertices.get(i), i);
        }

        UnionFind u = new UnionFind(vertices.size());
        for (Segment edge : graph.edges) {
            u.union(indices.get(edge.a), indices.get(edge.b));
        }

        Map<Integer, List<Segment>> connected = new TreeMap<>();
        for (Segment edge : graph.edges) {
            int i = u.find(indices.get(edge.a));
            List<Segment> list = connected.get(i);
            if (list == null) connected.put(i, list = new ArrayList<>());
            list.add(edge);
        }

        Set<Graph> r = new HashSet<>();
        for (List<Segment> segments : connected.values()) {
            r.add(new Graph(Graph.Type.CONNECTED, segments));
        }
        return r;
    }

    /**
     * 求连通图的外边界
     *
     * @param graph 连通图
     * @return 外边界，Polygon类型。如果不是连通图则返回null。
     */
    public static Polygon border(Graph graph) {
        if (graph.type != Graph.Type.CONNECTED && graph.type != Graph.Type.PLANAR) graph = graph.validate();
        if (graph.type != Graph.Type.CONNECTED && graph.type != Graph.Type.PLANAR) return null;

        Set<Point> vertices = graph.vertices;
        Map<Point, Set<Point>> edges = new HashMap<>();
        for (Segment e : graph.edges) {
            Set<Point> s1 = edges.get(e.a);
            Set<Point> s2 = edges.get(e.b);
            if (s1 == null) edges.put(e.a, s1 = new HashSet<>());
            if (s2 == null) edges.put(e.b, s2 = new HashSet<>());
            s1.add(e.b);
            s2.add(e.a);
        }

        Point from = vertices.iterator().next();
        {
            for (Point p : vertices) {
                if (p.x < from.x || (p.x == from.x && p.y < from.y)) {
                    from = p;
                }
            }
        }
        Point prev = from;
        Point curr = null;
        {
            Set<Point> points = edges.get(from);
            double max = -1000;
            for (Point t : points) {
                double angle = Math.atan2(t.y - from.y, t.x - from.x);
                if (angle > max) {
                    max = angle;
                    curr = t;
                }
            }
        }
        List<Point> r = new ArrayList<>();
        List<Point> bad = new ArrayList<>();
        r.add(curr);

        for (; ; ) {
            double max = -1000;
            Point maxPoint = null;
            if (curr == null) return null;
            Set<Point> points = edges.get(curr);
            if (points == null) return null;
            for (Point point : points) {
                if (!bad.contains(point) && !r.contains(point) && (r.size() >= 2 || point != from)) {
                    double v = -L.angle(prev, curr, point);
                    if (v > max) {
                        max = v;
                        maxPoint = point;
                    }
                }
            }
            if (maxPoint == null) {
                r.remove(curr);
                bad.add(curr);
                if (r.size() > 1) {
                    prev = r.get(r.size() - 2);
                    curr = r.get(r.size() - 1);
                } else {
                    prev = from;
                    curr = r.get(r.size() - 1);
                }
            } else {
                if (maxPoint == from) break;
                r.add(maxPoint);
                prev = curr;
                curr = maxPoint;
            }
        }
        r.add(from);

        return new Polygon(r);
    }

    /**
     * 用平面图切割平面，返回MultiPolygon，只考虑图内的部分，不考虑图外
     *
     * @param graph 平面图
     * @return
     */
    public static MultiPolygon cutPlaneByPlanarGraph(Graph graph) {
        if (graph.type != Graph.Type.PLANAR) graph = graph.validate();
        if (graph.type != Graph.Type.PLANAR) return null;

        List<Polygon> l = new ArrayList<>();

        Triangulation.CdtResult r = Triangulation.cdt(graph);
        if (r == null) return null;

        Map<Point, Set<Triangle>> pointTriangleMap = new HashMap<>();
        for (Triangle triangle : r.triangles) {
            Set<Triangle> s1 = pointTriangleMap.get(triangle.a);
            Set<Triangle> s2 = pointTriangleMap.get(triangle.b);
            Set<Triangle> s3 = pointTriangleMap.get(triangle.c);
            if (s1 == null) pointTriangleMap.put(triangle.a, s1 = new HashSet<>());
            if (s2 == null) pointTriangleMap.put(triangle.b, s2 = new HashSet<>());
            if (s3 == null) pointTriangleMap.put(triangle.c, s3 = new HashSet<>());
            s1.add(triangle);
            s2.add(triangle);
            s3.add(triangle);
        }

        Map<Segment, Triangle> edgeTriangle1 = new HashMap<>();
        Map<Segment, Triangle> edgeTriangle2 = new HashMap<>();
        for (Segment e : r.newEdges) {
            Set<Triangle> s1 = pointTriangleMap.get(e.a);
            Set<Triangle> s2 = pointTriangleMap.get(e.b);
            Set<Triangle> s = new HashSet<>(s1);
            s.retainAll(s2);
            Iterator<Triangle> ti = s.iterator();
            if (ti.hasNext()) edgeTriangle1.put(e, ti.next());
            if (ti.hasNext()) edgeTriangle2.put(e, ti.next());
        }

        Set<Triangle> triangles = new HashSet<>(r.triangles);
        while (!triangles.isEmpty()) {
            Triangle start = triangles.iterator().next();
            Set<Triangle> added = new HashSet<>();
            added.add(start);
            Queue<Triangle> queue = new ArrayDeque<>();
            queue.add(start);

            while (!queue.isEmpty()) {
                Triangle curr = queue.poll();
                Segment e1 = curr.ab;
                Segment e2 = curr.bc;
                Segment e3 = curr.ca;
                if (r.newEdges.contains(e1)) {
                    Triangle t1 = edgeTriangle1.get(e1);
                    Triangle t2 = edgeTriangle2.get(e1);
                    if (t1 != null && !added.contains(t1)) { queue.add(t1); added.add(t1); }
                    if (t2 != null && !added.contains(t2)) { queue.add(t2); added.add(t2); }
                }
                if (r.newEdges.contains(e2)) {
                    Triangle t1 = edgeTriangle1.get(e2);
                    Triangle t2 = edgeTriangle2.get(e2);
                    if (t1 != null && !added.contains(t1)) { queue.add(t1); added.add(t1); }
                    if (t2 != null && !added.contains(t2)) { queue.add(t2); added.add(t2); }
                }
                if (r.newEdges.contains(e3)) {
                    Triangle t1 = edgeTriangle1.get(e3);
                    Triangle t2 = edgeTriangle2.get(e3);
                    if (t1 != null && !added.contains(t1)) { queue.add(t1); added.add(t1); }
                    if (t2 != null && !added.contains(t2)) { queue.add(t2); added.add(t2); }
                }
            }

            Set<Segment> edges = new HashSet<>();
            for (Triangle triangle : added) {
                edges.add(triangle.ab);
                edges.add(triangle.bc);
                edges.add(triangle.ca);
            }
            Graph region = new Graph(edges);
            Polygon border = border(region);
            if (border != null) {
                l.add(border);
            }
            triangles.removeAll(added);
        }
        return new MultiPolygon(l);
    }
}
