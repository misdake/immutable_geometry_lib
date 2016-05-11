package geometry.shape;

import geometry.value.Vector;

public class Rect {
    public final Point  center;
    public final Vector xVector;
    public final Vector yVector;
    public final float  width;
    public final float  height;
    public final float  rotation;

    public Rect(Point center, float width, float height, float rotation) {
        throw new RuntimeException("not implemented");
    }
    public Rect(Point focus1, Point focus2, float halfWidth) {
        throw new RuntimeException("not implemented");
    }
}
