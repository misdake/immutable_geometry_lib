package com.rs.math.geometry.shape;

import com.rs.math.geometry.func.Collision;
import com.rs.math.geometry.func.Cut;

import java.util.*;

public class Graph {

    public enum Type {
        UNKNOWN,
        NORMAL,
        CONNECTED,
        PLANAR,
    }

    public final Type         type;
    public final Set<Point>   vertices;
    public final Set<Segment> edges;

    public Graph(Collection<Segment> edges) {
        Set<Point> v = new HashSet<>();
        Set<Segment> e = new HashSet<>();
        for (Segment edge : edges) {
            v.add(edge.a);
            v.add(edge.b);
        }
        e.addAll(edges);
        this.type = Type.UNKNOWN;
        this.vertices = Collections.unmodifiableSet(v);
        this.edges = Collections.unmodifiableSet(e);
    }

    public Graph(Type type, Collection<Segment> edges) {
        Set<Point> v = new HashSet<>();
        Set<Segment> e = new HashSet<>();
        for (Segment edge : edges) {
            v.add(edge.a);
            v.add(edge.b);
        }
        e.addAll(edges);

        this.type = type;
        this.vertices = Collections.unmodifiableSet(v);
        this.edges = Collections.unmodifiableSet(e);
    }

    private Graph(Type type, Set<Point> vertices, Set<Segment> edges) {
        this.type = type;
        this.vertices = vertices;
        this.edges = edges;
    }

    public Graph validate() {
        boolean connected = Cut.connectedGraphs(this).size() == 1;
        if (!connected) return new Graph(Type.NORMAL, vertices, edges);

        List<Segment> edges = new ArrayList<>(this.edges);
        for (int i = 0; i < edges.size() - 1; i++) {
            Segment s1 = edges.get(i);
            for (int j = i + 1; j < edges.size(); j++) {
                Segment s2 = edges.get(j);
                Collision.SegmentResult r = Collision.intersect(s1, s2);
                if (r.resultType != Collision.SegmentResultType.CONNECTED && r.resultType != Collision.SegmentResultType.NONE) {
                    return new Graph(Type.CONNECTED, this.vertices, this.edges);
                }
            }
        }
        return new Graph(Type.PLANAR, this.vertices, this.edges);
    }

    public Graph validate(List<Collision.SegmentResult> intersections) {
        boolean connected = Cut.connectedGraphs(this).size() == 1;
        if (!connected) return new Graph(Type.NORMAL, vertices, edges);

        boolean found = false;

        List<Segment> edges = new ArrayList<>(this.edges);
        for (int i = 0; i < edges.size() - 1; i++) {
            Segment s1 = edges.get(i);
            for (int j = i + 1; j < edges.size(); j++) {
                Segment s2 = edges.get(j);
                Collision.SegmentResult r = Collision.intersect(s1, s2);
                if (r.resultType != Collision.SegmentResultType.CONNECTED && r.resultType != Collision.SegmentResultType.NONE) {
                    found = true;
                    intersections.add(r);
                }
            }
        }

        if (found) {
            return new Graph(Type.CONNECTED, this.vertices, this.edges);
        } else {
            return new Graph(Type.PLANAR, this.vertices, this.edges);
        }
    }
}
