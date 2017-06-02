/*
 * ==============================BEGIN_COPYRIGHT===============================
 * =======Wangluo Electronic Technology Co., LTD. PROPRIETARY INFORMATION======
 * This software and its associated IndoorStar products are supplied under the
 * terms of a license agreement or nondisclosure agreement (NDA) with Wangluo
 * Electronic Technology Co., LTD., Shanghai and may not be copied or disclosed
 * except in accordance with the terms of that agreement.
 * Copyright (c) 2013 - 2015
 * Wangluo Electronic Technology Co., LTD., Shanghai. All Rights Reserved.
 * ===============================END_COPYRIGHT================================
 *
 * @author - IDS R&D Group
 * @date   - 2015-02-16 12:43
 */

package com.rs.math.geometry.util;

public class AABB {
    public static final double max = Double.MAX_VALUE;
    public static final double min = -Double.MAX_VALUE;

    double minX, minY;
    double maxX, maxY;

    public AABB() {
        reset();
    }

    public void reset() {
        minX = max;
        minY = max;
        maxX = min;
        maxY = min;
    }

    public void combine(AABB son) {
        if (son.minX < minX) minX = son.minX;
        if (son.minY < minY) minY = son.minY;
        if (son.maxX > maxX) maxX = son.maxX;
        if (son.maxY > maxY) maxY = son.maxY;
    }

    public void combine(double x, double y) {
        if (x < minX) minX = x;
        if (y < minY) minY = y;
        if (x > maxX) maxX = x;
        if (y > maxY) maxY = y;
    }

    public boolean pointInside(double x, double y) {
        return !pointOutSide(x, y);
    }
    public boolean pointOutSide(double x, double y) {
        return (x < minX || x > maxX || y < minY || y > maxY);
    }
    public boolean intersect(AABB other) {
        return !(this.maxX < other.minX || this.minX > other.maxX ||
                 this.maxY < other.minY || this.minY > other.maxY);
    }
    public boolean intersect(double minX, double maxX, double minY, double maxY) {
        return !(this.maxX < minX || this.minX > maxX ||
                 this.maxY < minY || this.minY > maxY);
    }

    public double getMinX() { return minX; }
    public double getMinY() { return minY; }
    public double getMaxX() { return maxX; }
    public double getMaxY() { return maxY; }

    public double dx()      { return maxX - minX; }
    public double dy()      { return maxY - minY; }
    public double r()       { return Math.hypot(dx(), dy()); }
    public double mx()      { return (maxX + minX) / 2; }
    public double my()      { return (maxY + minY) / 2; }
}
