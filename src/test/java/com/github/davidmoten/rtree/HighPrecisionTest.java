package com.github.davidmoten.rtree;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import org.junit.Test;

public class HighPrecisionTest {

    @Test
    public void testForIssue72() {
        long x = 123456789L;
        System.out.println(new BigDecimal(x).floatValue());
        BigDecimal b = new BigDecimal(x);
        System.out.println(b.round(FLOOR).floatValue());
        System.out.println(b.round(CEILING).floatValue());
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
        return new BigDecimal(a).compareTo(new BigDecimal(b)) >=0;
    }
    
}
