package com.github.davidmoten.rtree.geometry;

import com.github.davidmoten.rtree.geometry.internal.CircleFloat;
import com.github.davidmoten.rtree.geometry.internal.LineFloat;

import rx.functions.Func2;

public final class Intersects {

    private Intersects() {
        // prevent instantiation
    }

    public static final Func2<Rectangle, CircleFloat, Boolean> rectangleIntersectsCircle = new Func2<Rectangle, CircleFloat, Boolean>() {
        @Override
        public Boolean call(Rectangle rectangle, CircleFloat circle) {
            return circleIntersectsRectangle.call(circle, rectangle);
        }
    };

    public static final Func2<CircleFloat, Rectangle, Boolean> circleIntersectsRectangle = new Func2<CircleFloat, Rectangle, Boolean>() {
        @Override
        public Boolean call(CircleFloat circle, Rectangle rectangle) {
            return circle.intersects(rectangle);
        }
    };

    public static final Func2<Point, CircleFloat, Boolean> pointIntersectsCircle = new Func2<Point, CircleFloat, Boolean>() {
        @Override
        public Boolean call(Point point, CircleFloat circle) {
            return circleIntersectsPoint.call(circle, point);
        }
    };

    public static final Func2<CircleFloat, Point, Boolean> circleIntersectsPoint = new Func2<CircleFloat, Point, Boolean>() {
        @Override
        public Boolean call(CircleFloat circle, Point point) {
            return circle.intersects(point);
        }
    };

    public static final Func2<CircleFloat, CircleFloat, Boolean> circleIntersectsCircle = new Func2<CircleFloat, CircleFloat, Boolean>() {
        @Override
        public Boolean call(CircleFloat a, CircleFloat b) {
            return a.intersects(b);
        }
    };

    public static final Func2<LineFloat, LineFloat, Boolean> lineIntersectsLine = new Func2<LineFloat, LineFloat, Boolean>() {
        @Override
        public Boolean call(LineFloat a, LineFloat b) {
            return a.intersects(b);
        }
    };

    public static final Func2<LineFloat, Rectangle, Boolean> lineIntersectsRectangle = new Func2<LineFloat, Rectangle, Boolean>() {
        @Override
        public Boolean call(LineFloat a, Rectangle r) {
            return rectangleIntersectsLine.call(r, a);
        }
    };

    public static final Func2<Rectangle, LineFloat, Boolean> rectangleIntersectsLine = new Func2<Rectangle, LineFloat, Boolean>() {
        @Override
        public Boolean call(Rectangle r, LineFloat a) {
            return a.intersects(r);
        }
    };

    public static final Func2<LineFloat, CircleFloat, Boolean> lineIntersectsCircle = new Func2<LineFloat, CircleFloat, Boolean>() {
        @Override
        public Boolean call(LineFloat a, CircleFloat c) {
            return circleIntersectsLine.call(c, a);
        }
    };

    public static final Func2<CircleFloat, LineFloat, Boolean> circleIntersectsLine = new Func2<CircleFloat, LineFloat, Boolean>() {
        @Override
        public Boolean call(CircleFloat c, LineFloat a) {
            return a.intersects(c);
        }
    };

    public static final Func2<LineFloat, Point, Boolean> lineIntersectsPoint = new Func2<LineFloat, Point, Boolean>() {

        @Override
        public Boolean call(LineFloat line, Point point) {
            return pointIntersectsLine.call(point, line);
        }
    };

    public static final Func2<Point, LineFloat, Boolean> pointIntersectsLine = new Func2<Point, LineFloat, Boolean>() {

        @Override
        public Boolean call(Point point, LineFloat line) {
            return line.intersects(point);
        }
    };

    public static final Func2<Geometry, LineFloat, Boolean> geometryIntersectsLine = new Func2<Geometry, LineFloat, Boolean>() {

        @Override
        public Boolean call(Geometry geometry, LineFloat line) {
            if (geometry instanceof Line)
                return line.intersects((Line) geometry);
            else if (geometry instanceof CircleFloat)
                return line.intersects((Circle) geometry);
            else if (geometry instanceof Point)
                return line.intersects((Point) geometry);
            else if (geometry instanceof Rectangle)
                return line.intersects((Rectangle) geometry);
            else
                throw new RuntimeException("unrecognized geometry: " + geometry);
        }
    };

    public static final Func2<Geometry, Circle, Boolean> geometryIntersectsCircle = new Func2<Geometry, Circle, Boolean>() {

        @Override
        public Boolean call(Geometry geometry, Circle circle) {
            if (geometry instanceof Line)
                return circle.intersects((Line) geometry);
            else if (geometry instanceof Circle)
                return circle.intersects((Circle) geometry);
            else if (geometry instanceof Point)
                return circle.intersects((Point) geometry);
            else if (geometry instanceof Rectangle)
                return circle.intersects((Rectangle) geometry);
            else
                throw new RuntimeException("unrecognized geometry: " + geometry);
        }
    };

    public static final Func2<Circle, Geometry, Boolean> circleIntersectsGeometry = new Func2<Circle, Geometry, Boolean>() {

        @Override
        public Boolean call(Circle circle, Geometry geometry) {
            return geometryIntersectsCircle.call(geometry, circle);
        }
    };

    public static final Func2<Geometry, Rectangle, Boolean> geometryIntersectsRectangle = new Func2<Geometry, Rectangle, Boolean>() {

        @Override
        public Boolean call(Geometry geometry, Rectangle r) {
            if (geometry instanceof Line)
                return geometry.intersects(r);
            else if (geometry instanceof CircleFloat)
                return geometry.intersects(r);
            else if (geometry instanceof Point)
                return geometry.intersects(r);
            else if (geometry instanceof Rectangle)
                return r.intersects((Rectangle) geometry);
            else
                throw new RuntimeException("unrecognized geometry: " + geometry);
        }
    };

    public static final Func2<Rectangle, Geometry, Boolean> rectangleIntersectsGeometry = new Func2<Rectangle, Geometry, Boolean>() {

        @Override
        public Boolean call(Rectangle r, Geometry geometry) {
            return geometryIntersectsRectangle.call(geometry, r);
        }
    };

    public static final Func2<Geometry, Point, Boolean> geometryIntersectsPoint = new Func2<Geometry, Point, Boolean>() {

        @Override
        public Boolean call(Geometry geometry, Point point) {
            return geometryIntersectsRectangle.call(geometry, point.mbr());
        }
    };

    public static final Func2<Point, Geometry, Boolean> pointIntersectsGeometry = new Func2<Point, Geometry, Boolean>() {

        @Override
        public Boolean call(Point point, Geometry geometry) {
            return geometryIntersectsPoint.call(geometry, point);
        }
    };

}
