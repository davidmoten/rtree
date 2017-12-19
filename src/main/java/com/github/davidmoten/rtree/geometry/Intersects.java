package com.github.davidmoten.rtree.geometry;

import rx.functions.Func2;

public final class Intersects {

    private Intersects() {
        // prevent instantiation
    }

    public static final Func2<Rectangle, Circle, Boolean> rectangleIntersectsCircle = new Func2<Rectangle, Circle, Boolean>() {
        @Override
        public Boolean call(Rectangle rectangle, Circle circle) {
            return circleIntersectsRectangle.call(circle, rectangle);
        }
    };

    public static final Func2<Circle, Rectangle, Boolean> circleIntersectsRectangle = new Func2<Circle, Rectangle, Boolean>() {
        @Override
        public Boolean call(Circle circle, Rectangle rectangle) {
            return circle.intersects(rectangle);
        }
    };

    public static final Func2<Point, Circle, Boolean> pointIntersectsCircle = new Func2<Point, Circle, Boolean>() {
        @Override
        public Boolean call(Point point, Circle circle) {
            return circleIntersectsPoint.call(circle, point);
        }
    };

    public static final Func2<Circle, Point, Boolean> circleIntersectsPoint = new Func2<Circle, Point, Boolean>() {
        @Override
        public Boolean call(Circle circle, Point point) {
            return circle.intersects(point);
        }
    };

    public static final Func2<Circle, Circle, Boolean> circleIntersectsCircle = new Func2<Circle, Circle, Boolean>() {
        @Override
        public Boolean call(Circle a, Circle b) {
            return a.intersects(b);
        }
    };

    public static final Func2<Line, Line, Boolean> lineIntersectsLine = new Func2<Line, Line, Boolean>() {
        @Override
        public Boolean call(Line a, Line b) {
            return a.intersects(b);
        }
    };

    public static final Func2<Line, Rectangle, Boolean> lineIntersectsRectangle = new Func2<Line, Rectangle, Boolean>() {
        @Override
        public Boolean call(Line a, Rectangle r) {
            return rectangleIntersectsLine.call(r, a);
        }
    };

    public static final Func2<Rectangle, Line, Boolean> rectangleIntersectsLine = new Func2<Rectangle, Line, Boolean>() {
        @Override
        public Boolean call(Rectangle r, Line a) {
            return a.intersects(r);
        }
    };

    public static final Func2<Line, Circle, Boolean> lineIntersectsCircle = new Func2<Line, Circle, Boolean>() {
        @Override
        public Boolean call(Line a, Circle c) {
            return circleIntersectsLine.call(c, a);
        }
    };

    public static final Func2<Circle, Line, Boolean> circleIntersectsLine = new Func2<Circle, Line, Boolean>() {
        @Override
        public Boolean call(Circle c, Line a) {
            return a.intersects(c);
        }
    };

    public static final Func2<Line, Point, Boolean> lineIntersectsPoint = new Func2<Line, Point, Boolean>() {

        @Override
        public Boolean call(Line line, Point point) {
            return pointIntersectsLine.call(point, line);
        }
    };

    public static final Func2<Point, Line, Boolean> pointIntersectsLine = new Func2<Point, Line, Boolean>() {

        @Override
        public Boolean call(Point point, Line line) {
            return line.intersects(point);
        }
    };

    public static final Func2<Geometry, Line, Boolean> geometryIntersectsLine = new Func2<Geometry, Line, Boolean>() {

        @Override
        public Boolean call(Geometry geometry, Line line) {
            if (geometry instanceof Line)
                return line.intersects((Line) geometry);
            else if (geometry instanceof Circle)
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
            else if (geometry instanceof Circle)
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
