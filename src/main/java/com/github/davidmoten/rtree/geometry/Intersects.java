package com.github.davidmoten.rtree.geometry;

import rx.functions.Func2;

public final class Intersects {

    private Intersects() {
        // prevent instantiation
    }

    public static final Func2<Rectangle, Circle, Boolean> rectangleIntersectsCircle = new Func2<Rectangle, Circle, Boolean>() {
        @Override
        public Boolean call(Rectangle rectangle, Circle circle) {
            return circle.intersects(rectangle);
        }
    };

    public static final Func2<Point, Circle, Boolean> pointIntersectsCircle = new Func2<Point, Circle, Boolean>() {
        @Override
        public Boolean call(Point point, Circle circle) {
            return circle.intersects(point);
        }
    };

    public static final Func2<Circle, Circle, Boolean> circleIntersectsCircle = new Func2<Circle, Circle, Boolean>() {
        @Override
        public Boolean call(Circle a, Circle b) {
            return a.intersects(b);
        }
    };

}
