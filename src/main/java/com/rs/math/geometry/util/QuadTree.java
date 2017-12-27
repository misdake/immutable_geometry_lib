package com.rs.math.geometry.util;

import com.rs.math.geometry.shape.Point;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class QuadTree<T extends Point> {

    Node<T> root;

    public QuadTree(AABB aabb) {
        root = new Node<>(0, aabb.minX, aabb.maxX, aabb.minY, aabb.maxY);
    }

    public QuadTree(double minX, double maxX, double minY, double maxY) {
        root = new Node<>(0, minX, maxX, minY, maxY);
    }

    public void insert(T entry) {
        root.insert(entry, entry.x, entry.y);
    }

    public void trim() {
        root.trim();
    }

    public ArrayList<T> find(double x, double y, double max) {
        ArrayList<T> list = new ArrayList<>();
        root.find(list, x - max, x + max, y - max, y + max);
        for (Iterator<T> iterator = list.iterator(); iterator.hasNext(); ) {
            T t = iterator.next();
            double dx = t.x - x;
            double dy = t.y - y;
            if (dx * dx + dy * dy > max * max) {
                iterator.remove();
            }
        }
        return list;
    }
    public ArrayList<T> find(double minX, double maxX, double minY, double maxY) {
        ArrayList<T> list = new ArrayList<>();
        root.find(list, minX, maxX, minY, maxY);
        return list;
    }
    public ArrayList<T> find(AABB aabb) {
        ArrayList<T> list = new ArrayList<>();
        root.find(list, aabb.minX, aabb.maxX, aabb.minY, aabb.maxY);
        return list;
    }
    public T nearest(double x, double y, double max) {
        ArrayList<T> list = find(x - max, x + max, y - max, y + max);
        T p = null;
        double min = Double.MAX_VALUE;
        for (T point : list) {
            double dx = point.x - x;
            double dy = point.y - y;
            double d = dx * dx + dy * dy;
            if (d < min) {
                min = d;
                p = point;
            }
        }
        if (min < max * max) {
            return p;
        } else {
            return null;
        }
    }
    public T nearest(T entry, double max) {
        double x = entry.x;
        double y = entry.y;
        ArrayList<T> list = find(x - max, x + max, y - max, y + max);
        list.remove(entry);
        T p = null;
        double min = Double.MAX_VALUE;
        for (T point : list) {
            double dx = point.x - x;
            double dy = point.y - y;
            double d = dx * dx + dy * dy;
            if (d < min) {
                min = d;
                p = point;
            }
        }
        if (min < max * max) {
            return p;
        } else {
            return null;
        }
    }

    static final class Node<T extends Point> {

//        static int INSERT_COUNT = 0;
//        static int COPY_SUM     = 0;
//        static int SEARCH_COUNT = 0;

        static int MAX_DEPTH      = 10;
        static int INIT_COMPONENT = 5;
        static int MAX_COMPONENT  = 10;

        final int    depth;
        final double minX;
        final double minY;
        final double maxX;
        final double maxY;
        final double midX;
        final double midY;

        Node<T> n00;
        Node<T> n01;
        Node<T> n10;
        Node<T> n11;

        int     size;
        Point[] array;

        Node(int depth, double minX, double maxX, double minY, double maxY) {
            this.depth = depth;
            this.minX = minX;
            this.maxX = maxX;
            this.minY = minY;
            this.maxY = maxY;
            this.midX = (minX + maxX) / 2;
            this.midY = (minY + maxY) / 2;

            this.n00 = null;
            this.n01 = null;
            this.n10 = null;
            this.n11 = null;

            this.size = 0;
            this.array = null;
        }

        void insert(Point entry, double x, double y) {
            assert minX <= x && x <= maxX && minY <= y && y <= maxY;
            if (depth == MAX_DEPTH) {
                if (array == null) allocArray(INIT_COMPONENT);
                if (array.length == size) {
                    Point[] old = array;
                    int newSize = size * 2 + 1;
                    allocArray(newSize);
                    System.arraycopy(old, 0, array, 0, size);
//                    COPY_SUM += size;
                }
                array[size++] = entry;
//                INSERT_COUNT++;
            } else if (size >= 0 && size < MAX_COMPONENT) {
                if (array == null) allocArray(MAX_COMPONENT);
                array[size++] = entry;
//                INSERT_COUNT++;
            } else {
                if (size == MAX_COMPONENT) {
                    subdivide();
                }
                node(x, y).insert(entry, x, y);
            }
        }
        private void allocArray(int newSize) {
            array = new Point[newSize];
        }
        void trim() {
            if (size >= 0) {
                Point[] old = array;
                int newSize = size;
                allocArray(newSize);
                System.arraycopy(old, size, array, 0, size);
            } else {
                n00.trim();
                n01.trim();
                n10.trim();
                n11.trim();
            }
        }
        private Node<T> node(double x, double y) {
            assert minX <= x && x <= maxX && minY <= y && y <= maxY;
            if (x < midX && y < midY) return n00;
            if (x < midX && y >= midY) return n01;
            if (x >= midX && y < midY) return n10;
            if (x >= midX && y >= midY) return n11;
            throw new AssertionError("not possible to get here");
        }

        private void subdivide() {
            n00 = new Node<>(depth + 1, minX, midX, minY, midY);
            n01 = new Node<>(depth + 1, minX, midX, midY, maxY);
            n10 = new Node<>(depth + 1, midX, maxX, minY, midY);
            n11 = new Node<>(depth + 1, midX, maxX, midY, maxY);
            if (array != null) {
                for (int i = 0; i < size; i++) {
                    Point t = array[i];
                    double x = t.x;
                    double y = t.y;
                    node(x, y).insert(t, x, y);
                }
            }
            array = null;
            size = -1;
        }

        void find(Collection<T> result, double minX, double maxX, double minY, double maxY) {
            if (this.maxX < minX || this.minX > maxX || this.maxY < minY || this.minY > maxY) return;
//            SEARCH_COUNT++;
            if (size >= 0) {
                for (int i = 0; i < size; i++) {
                    Point point = array[i];
                    double x = point.x;
                    double y = point.y;
                    if (minX < x && x <= maxX && minY < y && y <= maxY) {
                        //noinspection unchecked
                        result.add((T) point);
                    }
                }
            } else {
                n00.find(result, minX, maxX, minY, maxY);
                n01.find(result, minX, maxX, minY, maxY);
                n10.find(result, minX, maxX, minY, maxY);
                n11.find(result, minX, maxX, minY, maxY);
            }
        }

    }
}
