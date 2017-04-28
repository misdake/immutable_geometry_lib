package com.rs.math.geometry.func;

import com.rs.math.geometry.shape.Point;
import com.rs.math.geometry.shape.Polygon;
import com.rs.math.geometry.util.AABB;

import java.util.List;
import java.util.PriorityQueue;

public class Center {
    
    public static Point centroid(Polygon polygon) {
        List<Point> points = polygon.points;
        double area2 = 0;
        double accX = 0;
        double accY = 0;
        for (int i = 0; i < points.size(); i++) {
            Point p1 = points.get(i);
            Point p2 = points.get((i + 1) % points.size());
            double c = p1.x * p2.y - p2.x * p1.y;
            area2 += c;
            accX += (p1.x + p2.x) * c;
            accY += (p1.y + p2.y) * c;
        }
        double x = accX / 6 / (area2 / 2);
        double y = accY / 6 / (area2 / 2);
        return new Point(x, y);
    }
    
    public static Point visualCenter(Polygon polygon, double precision) {
        List<Point> points = polygon.points;

        AABB aabb = new AABB();
        for (Point point : points) {
            aabb.combine(point.x, point.y);
        }
        double minX = aabb.getMinX();
        double maxX = aabb.getMaxX();
        double minY = aabb.getMinY();
        double maxY = aabb.getMaxY();
        double width = aabb.dx();
        double height = aabb.dy();
        double cellSize = Math.min(width, height);
        double h = cellSize / 2;

        PriorityQueue<Cell> cellQueue = new PriorityQueue<>();
        for (double x = minX; x < maxX; x += cellSize) {

            for (double y = minY; y < maxY; y += cellSize) {
                cellQueue.add(new Cell(x + h, y + h, h, polygon));
            }
        }
        Point c = centroid(polygon);
        Cell bestCell = new Cell(c.x, c.y, 0, polygon);
        Cell bboxCell = new Cell(minX + width / 2, minY + height / 2, 0, polygon);
        if (bboxCell.d > bestCell.d) bestCell = bboxCell;

        int numProbes = cellQueue.size();

        while (cellQueue.size() > 0) {
            Cell cell = cellQueue.poll();

            if (cell.d > bestCell.d) {
                bestCell = cell;
//                System.out.printf("found best %f after %d probes\n", Math.round(1e4 * cell.d) / 1e4, numProbes);
            }
            if (cell.max - bestCell.d <= precision) continue;

            // split the cell into four cells
            h = cell.h / 2;
            cellQueue.add(new Cell(cell.x - h, cell.y - h, h, polygon));
            cellQueue.add(new Cell(cell.x + h, cell.y - h, h, polygon));
            cellQueue.add(new Cell(cell.x - h, cell.y + h, h, polygon));
            cellQueue.add(new Cell(cell.x + h, cell.y + h, h, polygon));
            numProbes += 4;
        }

//        System.out.println("num probes: " + numProbes);
//        System.out.println("best distance: " + bestCell.d);
        return new Point(bestCell.x, bestCell.y);
    }
    private static class Cell implements Comparable<Cell> {
        double x, y, h, d, max;
        Cell(double x, double y, double h, Polygon polygon) {
            this.x = x;
            this.y = y;
            this.h = h;
            this.d = pointToPolygonDist(x, y, polygon);
            this.max = this.d + this.h * Math.sqrt(2);
        }
        @Override
        public int compareTo(Cell o) {
            return Double.compare(o.max, this.max);
        }
        // signed distance from point to polygon outline (negative if point is outside)
        private double pointToPolygonDist(double x, double y, Polygon polygon) {
            boolean inside = Collision.in(new Point(x, y), polygon);
            double minDistSq = Double.MAX_VALUE;
            for (int i = 0, len = polygon.points.size(), j = len - 1; i < len; j = i++) {
                Point p1 = polygon.points.get(i);
                Point p2 = polygon.points.get(j);
                minDistSq = Math.min(minDistSq, getSegDistSq(x, y, p1, p2));
            }
            return (inside ? 1 : -1) * Math.sqrt(minDistSq);
        }
        // get squared distance from a point to a segment
        private double getSegDistSq(double px, double py, Point a, Point b) {
            double x = a.x;
            double y = a.y;
            double dx = b.x - x;
            double dy = b.y - y;

            if (dx != 0 || dy != 0) {
                double t = ((px - x) * dx + (py - y) * dy) / (dx * dx + dy * dy);
                if (t > 1) {
                    x = b.x;
                    y = b.y;
                } else if (t > 0) {
                    x += dx * t;
                    y += dy * t;
                }
            }
            dx = px - x;
            dy = py - y;
            return dx * dx + dy * dy;
        }
    }
    
}
