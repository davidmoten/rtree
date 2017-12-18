package com.github.davidmoten.rtree;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import org.junit.Test;

import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Rectangle;

public class HighPrecisionTest {

    @Test
    public void testForIssue72() {
        long x = 123456789L;
        System.out.println(new BigDecimal(x).floatValue());
        BigDecimal b = new BigDecimal(x);
        System.out.println(b.round(FLOOR).floatValue());
        System.out.println(b.round(CEILING).floatValue());
    }

    @Test
    public void testHighPrecision() {
        RTree<Integer, Rectangle> tree = RTree.create();
        tree = tree.add(1, Geometries.rectangle(0, 0, 1, 1));
        double x1 = 2.0000000001;
        System.out.println((float) x1);
        tree = tree.add(2, Geometries.rectangle(x1, 2, 3, 3));
        tree.search(Geometries.rectangle((float) x1, 2.0, (float) x1, 2.0)) //
                .test() //
                .assertNoValues() //
                .assertCompleted();
        tree.search(Geometries.rectangle(x1, 2.0, x1, 2.0)) //
                .test() //
                .assertValueCount(1) //
                .assertCompleted();
    }

    private static final MathContext FLOOR = new MathContext(7, RoundingMode.FLOOR);
    private static final MathContext CEILING = new MathContext(7, RoundingMode.CEILING);

    private static float floor(long x) {
        return new BigDecimal(x).round(FLOOR).floatValue();
    }

    private static float ceil(long x) {
        return new BigDecimal(x).round(CEILING).floatValue();
    }

    private static boolean gte(float a, long b) {
        return new BigDecimal(a).compareTo(new BigDecimal(b)) >= 0;
    }

}
