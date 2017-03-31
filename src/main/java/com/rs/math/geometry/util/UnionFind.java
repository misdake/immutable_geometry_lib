package com.rs.math.geometry.util;

public class UnionFind {

    public int[] parent;

    public UnionFind(int n) {
        parent = new int[n];
        for (int i = 0; i < parent.length; i++) {
            parent[i] = i;
        }
    }

    public int find(int e) {
        int root = e;
        while (parent[root] != root) {
            root = parent[root];
        }
        while (parent[e] != e) {
            int next = parent[e];
            parent[e] = root;
            e = next;
        }
        return root;
    }

    public void union(int i, int j) {
        int vi = find(i);
        int vj = find(j);
        if (vi != vj) {
            parent[vj] = vi;
        }
    }

}
