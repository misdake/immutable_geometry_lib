package com.rs.math.geometry.func;

import com.rs.math.geometry.shape.*;
import org.jdelaunay.delaunay.ConstrainedMesh;
import org.jdelaunay.delaunay.geometries.DEdge;
import org.jdelaunay.delaunay.geometries.DPoint;
import org.jdelaunay.delaunay.geometries.DTriangle;

import java.util.*;

public class Triangulation {

    public static class CdtResult {
        public final Set<Point>    points;
        public final Set<Segment>  constraints;
        public final Set<Segment>  newEdges;
        public final Set<Triangle> triangles;
        public CdtResult(Set<Point> points, Set<Segment> constraints, Set<Segment> newEdges, Set<Triangle> triangles) {
            this.points = points;
            this.constraints = constraints;
            this.newEdges = newEdges;
            this.triangles = triangles;
        }
    }

    public static CdtResult cdt(Collection<Point> points, Collection<Segment> edges) {
        try {
            ConstrainedMesh mesh = new ConstrainedMesh();

            for (Point point : points) {
                DPoint p = new DPoint(point.x, point.y, 0);
                mesh.addPoint(p);
            }
            for (Segment line : edges) {
                DEdge e = new DEdge(line.a.x, line.a.y, 0, line.b.x, line.b.y, 0);
                mesh.addConstraintEdge(e);
            }
            mesh.forceConstraintIntegrity();
            mesh.processDelaunay();

            Map<DPoint, Point> pointMap = new HashMap<>();
            Set<Point> points_f = new HashSet<>();
            Set<Segment> constraints_f = new HashSet<>();
            Set<Segment> newEdges_f = new HashSet<>();
            Set<Triangle> triangles_f = new HashSet<>();

            Map<Point, Map<Point, Segment>> pointPairToEdge = new HashMap<>();

            for (DPoint p : mesh.getPoints()) {
                Point point = new Point(p.getX(), p.getY());
                pointMap.put(p, point);
                points_f.add(point);
                pointPairToEdge.put(point, new HashMap<Point, Segment>());
            }
            Set<DEdge> constraintEdgesSet = new HashSet<>();
            for (DEdge e : mesh.getConstraintEdges()) {
                constraintEdgesSet.add(e);
                Point v1 = pointMap.get(e.getStartPoint());
                Point v2 = pointMap.get(e.getEndPoint());
                Segment e1 = new Segment(v1, v2);
                constraints_f.add(e1);
                pointPairToEdge.get(v1).put(v2, e1);
                pointPairToEdge.get(v2).put(v1, e1);
            }
            for (DEdge e : mesh.getEdges()) {
                if (constraintEdgesSet.contains(e)) continue;
                constraintEdgesSet.add(e);
                Point v1 = pointMap.get(e.getStartPoint());
                Point v2 = pointMap.get(e.getEndPoint());
                Segment e1 = new Segment(v1, v2);
                newEdges_f.add(e1);
                pointPairToEdge.get(v1).put(v2, e1);
                pointPairToEdge.get(v2).put(v1, e1);
            }
            for (DTriangle t : mesh.getTriangleList()) {
                List<DPoint> p = t.getPoints();
                Point v1 = pointMap.get(p.get(0));
                Point v2 = pointMap.get(p.get(1));
                Point v3 = pointMap.get(p.get(2));
                Segment e12 = pointPairToEdge.get(v1).get(v2);
                Segment e23 = pointPairToEdge.get(v2).get(v3);
                Segment e31 = pointPairToEdge.get(v3).get(v1);
                triangles_f.add(new Triangle(v1, v2, v3, e12, e23, e31));
            }

            return new CdtResult(
                    Collections.unmodifiableSet(points_f),
                    Collections.unmodifiableSet(constraints_f),
                    Collections.unmodifiableSet(newEdges_f),
                    Collections.unmodifiableSet(triangles_f)
            );
        } catch (Exception ignored) {
            return null;
        }
    }


    public static CdtResult cdt(Graph graph) {
        Polygon border = Cut.border(graph);
        if (border == null) return null;
        CdtResult rr = cdt(graph.vertices, graph.edges);
        if (rr == null) return null;

        Set<Triangle> triangles = new HashSet<>(rr.triangles);
        Set<Segment> newSegments = new HashSet<>(rr.newEdges);
        for (Iterator<Triangle> iterator = triangles.iterator(); iterator.hasNext(); ) {
            Triangle triangle = iterator.next();
            double cx = (triangle.a.x + triangle.b.x + triangle.c.x) / 3f;
            double cy = (triangle.a.y + triangle.b.y + triangle.c.y) / 3f;
            if (!Collision.in(new Point(cx, cy), border)) {
                iterator.remove();
            }
        }
        for (Iterator<Segment> iterator = newSegments.iterator(); iterator.hasNext(); ) {
            Segment segment = iterator.next();
            double cx = (segment.a.x + segment.b.x) / 2f;
            double cy = (segment.a.y + segment.b.y) / 2f;
            if (!Collision.in(new Point(cx, cy), border)) {
                iterator.remove();
            }
        }

        return new CdtResult(
                rr.points,
                rr.constraints,
                Collections.unmodifiableSet(newSegments),
                Collections.unmodifiableSet(triangles)
        );
    }

}
