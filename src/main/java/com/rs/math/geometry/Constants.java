package com.rs.math.geometry;

public class Constants {
    public static final float EPSILON = 0.0001f;

    public static boolean equal(float a, float b) {
        return Math.abs(a - b) < EPSILON;
    }
}
