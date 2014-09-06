package com.github.davidmoten.rtree;

import static com.google.common.base.Optional.of;

import java.util.List;

import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.geometry.Rectangle;
import com.google.common.base.Optional;

public class SelectorMinimalMargin implements Selector {

    @Override
    public <T> Node<T> select(Geometry g, List<? extends Node<T>> nodes) {
        return findLeastPerimeter(g.mbr(), nodes);
    }

    static <T> Node<T> findLeastPerimeter(Rectangle r, List<? extends Node<T>> nodes) {

        Optional<Node<T>> least = Optional.absent();
        Optional<Double> leastMetric = Optional.absent();
        Optional<Double> leastArea = Optional.absent();
        for (Node<T> node : nodes) {
            double area = node.geometry().mbr().area();
            double m = node.geometry().mbr().add(r).perimeter();
            if (!least.isPresent() || m < leastMetric.get()) {
                least = of(node);
                leastMetric = of(m);
                leastArea = of(area);
            } else if (least.isPresent() && m == leastMetric.get() && area < leastArea.get()) {
                least = of(node);
                leastMetric = of(m);
                leastArea = of(area);
            }
        }
        return least.get();
    }

}
