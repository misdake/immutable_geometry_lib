package com.rs.math.geometry.util;

import java.util.ArrayList;
import java.util.Collection;

public class QuadTree2<T extends AABB> {

    Node<T> root;

    public QuadTree2(double minX, double maxX, double minY, double maxY) {
        root = new Node<>(0, minX, maxX, minY, maxY);
    }

    public void insert(T entry) {
        root.insert(entry);
    }
    public void remove(T entry) {
        root.remove(entry);
    }

    public void trim() {
        root.trim();
    }

    public ArrayList<T> find(double minX, double maxX, double minY, double maxY) {
        ArrayList<T> list = new ArrayList<>();
        root.find(list, minX, maxX, minY, maxY);
        return list;
    }

    static final class Node<T extends AABB> {

//        static int INSERT_COUNT = 0;
//        static int COPY_SUM     = 0;
//        static int SEARCH_COUNT = 0;

        static int MAX_DEPTH     = 10;
        static int MAX_COMPONENT = 10;

        final int    depth;
        final double minX;
        final double minY;
        final double maxX;
        final double maxY;
        final double midX;
        final double midY;

        ArrayList<AABB> children;
        Node[]          n;

        ArrayList<AABB> content;

        Node(int depth, double minX, double maxX, double minY, double maxY) {
            this.depth = depth;
            this.minX = minX;
            this.maxX = maxX;
            this.minY = minY;
            this.maxY = maxY;
            this.midX = (minX + maxX) / 2;
            this.midY = (minY + maxY) / 2;

            n = null;
            children = null;
            content = null;
        }

        void insert(AABB entry) {
            assert minX <= entry.minX && entry.maxX <= maxX && minY <= entry.minY && entry.maxY <= maxY;

            int nodeIndex = nodeIndex(entry);
            if (nodeIndex == -1) { //here
                if (content == null) content = new ArrayList<>();
                content.add(entry);
            } else { //n0123
                if (children == null && n == null) children = new ArrayList<>();
                if (children != null) {
                    if (depth == MAX_DEPTH || children.size() < MAX_COMPONENT) {
                        children.add(entry);
                        return;
                    } else {
                        subdivide();
                    }
                }
                n[nodeIndex].insert(entry);
            }
        }
        void remove(AABB entry) {
            assert minX <= entry.minX && entry.maxX <= maxX && minY <= entry.minY && entry.maxY <= maxY;
            int nodeIndex = nodeIndex(entry);
            if (nodeIndex == -1) {
                content.remove(entry);
            } else {
                if (children != null) {
                    children.remove(entry);
                }
                if (n != null) {
                    n[nodeIndex].remove(entry);
                }
            }
        }
        void trim() {
            if (children != null) children.trimToSize();
            if (content != null) content.trimToSize();
            if (n != null) {
                n[0].trim();
                n[1].trim();
                n[2].trim();
                n[3].trim();
            }
        }
        private int nodeIndex(AABB entry) {
            assert minX <= entry.minX && entry.maxX <= maxX && minY <= entry.minY && entry.maxY <= maxY;
            if (entry.maxX < midX && entry.maxY < midY) return 0;
            if (entry.maxX < midX && entry.minY >= midY) return 1;
            if (entry.minX >= midX && entry.maxY < midY) return 2;
            if (entry.minX >= midX && entry.minY >= midY) return 3;
            return -1;
        }

        private void subdivide() {
            n = new Node[]{
                    new Node<>(depth + 1, minX, midX, minY, midY),
                    new Node<>(depth + 1, minX, midX, midY, maxY),
                    new Node<>(depth + 1, midX, maxX, minY, midY),
                    new Node<>(depth + 1, midX, maxX, midY, maxY)
            };
            if (children != null) {
                for (AABB child : children) {
                    n[nodeIndex(child)].insert(child);
                }
            }
            children = null;
        }

        @SuppressWarnings("unchecked")
        void find(Collection<T> result, double minX, double maxX, double minY, double maxY) {
            if (this.maxX < minX || this.minX > maxX || this.maxY < minY || this.minY > maxY) return;
//            SEARCH_COUNT++;
            if (children != null) for (AABB child : children) {
                if (child.intersect(minX, maxX, minY, maxY)) {
                    result.add((T) child);
                }
            }
            if (content != null) for (AABB child : content) {
                if (child.intersect(minX, maxX, minY, maxY)) {
                    result.add((T) child);
                }
            }
            if (n != null) for (Node node : n) {
                node.find(result, minX, maxX, minY, maxY);
            }
        }

    }
}