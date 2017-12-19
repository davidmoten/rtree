package com.github.davidmoten.rtree.geometry.internal;

final class Vector {

    final double x;
    final double y;

    static Vector create(double x, double y) {
        return new Vector(x, y);
    }

    Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    double dot(Vector v) {
        return x * v.x + y * v.y;
    }

    Vector times(double value) {
        return create(value * x, value * y);
    }

    Vector minus(Vector v) {
        return create(x - v.x, y - v.y);
    }

    double modulus() {
        return Math.sqrt(modulusSquared());
    }

    double modulusSquared() {
        return x * x + y * y;
    }

}
