package com.github.davidmoten.rtree.geometry;

import io.reactivex.functions.BiFunction;

public final class Intersects {

    private Intersects() {
        // prevent instantiation
    }

    public static final BiFunction<Rectangle, Circle, Boolean> rectangleIntersectsCircle = new BiFunction<Rectangle, Circle, Boolean>() {
        @Override
        public Boolean apply(Rectangle rectangle, Circle circle) throws Exception {
            return circleIntersectsRectangle.apply(circle, rectangle);
        }
    };

    public static final BiFunction<Circle, Rectangle, Boolean> circleIntersectsRectangle = new BiFunction<Circle, Rectangle, Boolean>() {
        @Override
        public Boolean apply(Circle circle, Rectangle rectangle) {
            return circle.intersects(rectangle);
        }
    };

    public static final BiFunction<Point, Circle, Boolean> pointIntersectsCircle = new BiFunction<Point, Circle, Boolean>() {
        @Override
        public Boolean apply(Point point, Circle circle) throws Exception {
            return circleIntersectsPoint.apply(circle, point);
        }
    };

    public static final BiFunction<Circle, Point, Boolean> circleIntersectsPoint = new BiFunction<Circle, Point, Boolean>() {
        @Override
        public Boolean apply(Circle circle, Point point) {
            return circle.intersects(point);
        }
    };

    public static final BiFunction<Circle, Circle, Boolean> circleIntersectsCircle = new BiFunction<Circle, Circle, Boolean>() {
        @Override
        public Boolean apply(Circle a, Circle b) {
            return a.intersects(b);
        }
    };

    public static final BiFunction<Line, Line, Boolean> lineIntersectsLine = new BiFunction<Line, Line, Boolean>() {
        @Override
        public Boolean apply(Line a, Line b) {
            return a.intersects(b);
        }
    };

    public static final BiFunction<Line, Rectangle, Boolean> lineIntersectsRectangle = new BiFunction<Line, Rectangle, Boolean>() {
        @Override
        public Boolean apply(Line a, Rectangle r) throws Exception {
            return rectangleIntersectsLine.apply(r, a);
        }
    };

    public static final BiFunction<Rectangle, Line, Boolean> rectangleIntersectsLine = new BiFunction<Rectangle, Line, Boolean>() {
        @Override
        public Boolean apply(Rectangle r, Line a) {
            return a.intersects(r);
        }
    };

    public static final BiFunction<Line, Circle, Boolean> lineIntersectsCircle = new BiFunction<Line, Circle, Boolean>() {
        @Override
        public Boolean apply(Line a, Circle c) throws Exception {
            return circleIntersectsLine.apply(c, a);
        }
    };

    public static final BiFunction<Circle, Line, Boolean> circleIntersectsLine = new BiFunction<Circle, Line, Boolean>() {
        @Override
        public Boolean apply(Circle c, Line a) {
            return a.intersects(c);
        }
    };

    public static final BiFunction<Line, Point, Boolean> lineIntersectsPoint = new BiFunction<Line, Point, Boolean>() {

        @Override
        public Boolean apply(Line line, Point point) throws Exception {
            return pointIntersectsLine.apply(point, line);
        }
    };

    public static final BiFunction<Point, Line, Boolean> pointIntersectsLine = new BiFunction<Point, Line, Boolean>() {

        @Override
        public Boolean apply(Point point, Line line) {
            return line.intersects(point);
        }
    };

    public static final BiFunction<Geometry, Line, Boolean> geometryIntersectsLine = new BiFunction<Geometry, Line, Boolean>() {

        @Override
        public Boolean apply(Geometry geometry, Line line) {
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

    public static final BiFunction<Geometry, Circle, Boolean> geometryIntersectsCircle = new BiFunction<Geometry, Circle, Boolean>() {

        @Override
        public Boolean apply(Geometry geometry, Circle circle) {
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

    public static final BiFunction<Circle, Geometry, Boolean> circleIntersectsGeometry = new BiFunction<Circle, Geometry, Boolean>() {

        @Override
        public Boolean apply(Circle circle, Geometry geometry) throws Exception {
            return geometryIntersectsCircle.apply(geometry, circle);
        }
    };

    public static final BiFunction<Geometry, Rectangle, Boolean> geometryIntersectsRectangle = new BiFunction<Geometry, Rectangle, Boolean>() {

        @Override
        public Boolean apply(Geometry geometry, Rectangle r) {
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

    public static final BiFunction<Rectangle, Geometry, Boolean> rectangleIntersectsGeometry = new BiFunction<Rectangle, Geometry, Boolean>() {

        @Override
        public Boolean apply(Rectangle r, Geometry geometry) throws Exception {
            return geometryIntersectsRectangle.apply(geometry, r);
        }
    };

    public static final BiFunction<Geometry, Point, Boolean> geometryIntersectsPoint = new BiFunction<Geometry, Point, Boolean>() {

        @Override
        public Boolean apply(Geometry geometry, Point point) throws Exception {
            return geometryIntersectsRectangle.apply(geometry, point.mbr());
        }
    };

    public static final BiFunction<Point, Geometry, Boolean> pointIntersectsGeometry = new BiFunction<Point, Geometry, Boolean>() {

        @Override
        public Boolean apply(Point point, Geometry geometry) throws Exception {
            return geometryIntersectsPoint.apply(geometry, point);
        }
    };

}
