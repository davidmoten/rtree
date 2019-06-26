package com.github.davidmoten.rtree.internal;

import java.awt.geom.Line2D;

public final class RectangleUtil {

    private RectangleUtil() {
        // prevent instantiation
    }

    /**
     * The bitmask that indicates that a point lies to the left of this
     * <code>Rectangle2D</code>.
     * 
     * @since 1.2
     */
    public static final int OUT_LEFT = 1;

    /**
     * The bitmask that indicates that a point lies above this
     * <code>Rectangle2D</code>.
     * 
     * @since 1.2
     */
    public static final int OUT_TOP = 2;

    /**
     * The bitmask that indicates that a point lies to the right of this
     * <code>Rectangle2D</code>.
     * 
     * @since 1.2
     */
    public static final int OUT_RIGHT = 4;

    /**
     * The bitmask that indicates that a point lies below this
     * <code>Rectangle2D</code>.
     * 
     * @since 1.2
     */
    public static final int OUT_BOTTOM = 8;
    
    public static boolean rectangleIntersectsLine(double rectX, double rectY, double rectWidth, double rectHeight,
            double x1, double y1, double x2, double y2) {
        return _rectangleIntersectsLine(rectX, rectY, rectWidth, rectHeight, x1, y1, x2, y2)
                || Line2D.Double.linesIntersect(rectX, rectY, rectX + rectWidth, rectY + rectHeight, x1, y1, x2, y2);
    }
    
    private static boolean _rectangleIntersectsLine(double rectX, double rectY, double rectWidth,
            double rectHeight, double x1, double y1, double x2, double y2) {
        if (rectangleCornerOnSegment(rectX, rectY, rectWidth, rectHeight, x1, y1, x2, y2)) {
            return true;
        }
        int out1, out2;
        if ((out2 = outcode(rectX, rectY, rectWidth, rectHeight, x2, y2)) == 0) {
            return true;
        }
        while ((out1 = outcode(rectX, rectY, rectWidth, rectHeight, x1, y1)) != 0) {
            if ((out1 & out2) != 0) {
                return false;
            }
            if ((out1 & (OUT_LEFT | OUT_RIGHT)) != 0) {
                double x = rectX;
                if ((out1 & OUT_RIGHT) != 0) {
                    x += rectWidth;
                }
                y1 = y1 + (x - x1) * (y2 - y1) / (x2 - x1);
                x1 = x;
            } else {
                double y = rectY;
                if ((out1 & OUT_BOTTOM) != 0) {
                    y += rectHeight;
                }
                x1 = x1 + (y - y1) * (x2 - x1) / (y2 - y1);
                y1 = y;
            }
        }
        return true;
    }

    private static boolean rectangleCornerOnSegment(double rectX, double rectY, double rectWidth,
            double rectHeight, double x1, double y1, double x2, double y2) {
        if (pointOnSegment(rectX, rectY, x1, y1, x2, y2)) {
            return true;
        } else if (pointOnSegment(rectX + rectWidth, rectY, x1, y1, x2, y2)) {
            return true;
        } else if (pointOnSegment(rectX, rectY + rectHeight, x1, y1, x2, y2)) {
            return true;
        } else if (pointOnSegment(rectX + rectWidth, rectY + rectHeight, x1, y1, x2, y2)) {
            return true;
        } else {
            return false;
        }
    }

    private static final double PRECISION = 0.00000001;

    private static boolean pointOnSegment(double x, double y, double x1, double y1, double x2,
            double y2) {
        if (x < x1 || x > x2 || y < y1 || y > y2) {
            return false;
        } else {
            double v = (y2 - y1) * (x - x1) - (x2 - x1) * (y - y1);
            return Math.abs(v) < PRECISION;
        }
    }

    private static int outcode(double rectX, double rectY, double rectWidth, double rectHeight,
            double x, double y) {
        int out = 0;
        if (rectWidth <= 0) {
            out |= OUT_LEFT | OUT_RIGHT;
        } else if (x < rectX) {
            out |= OUT_LEFT;
        } else if (x > rectX + rectWidth) {
            out |= OUT_RIGHT;
        }
        if (rectHeight <= 0) {
            out |= OUT_TOP | OUT_BOTTOM;
        } else if (y < rectY) {
            out |= OUT_TOP;
        } else if (y > rectY + rectHeight) {
            out |= OUT_BOTTOM;
        }
        return out;
    }

}
