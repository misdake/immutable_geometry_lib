package com.rs.math.geometry.util;

/*
 * 改进的并查集，支持压缩路径
 */
public class UnionFind {

    public int[] parent;

    public UnionFind(int n) {
        parent = new int[n];
        for (int i = 0; i < parent.length; i++) {
            parent[i] = i;
        }
    }
    /*在查找类别的同时进行路径压缩
     * 进行路径压缩可以减少查找的时间
     */
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
