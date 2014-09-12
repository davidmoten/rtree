package com.github.davidmoten.rtree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.github.davidmoten.rtree.geometry.HasGeometry;
import com.github.davidmoten.rtree.geometry.Rectangle;
import com.google.common.base.Preconditions;

/**
 * @author dxm
 *
 */
public final class Util {

    private Util() {
        // prevent instantiation
    }

    static void instantiateForTestCoveragePurposesOnly() {
        new Util();
    }

    /**
     * Returns the minimum bounding rectangle of a number of items. Benchmarks
     * below indicate that when the number of items is >1 this method is more
     * performant than one using {@link Rectangle#add(Rectangle)}.
     * 
     * <pre>
     * Benchmark                             Mode  Samples         Score  Score error  Units
     * c.g.d.r.BenchmarksMbr.mbrList1       thrpt       10  48450492.301   436127.960  ops/s
     * c.g.d.r.BenchmarksMbr.mbrList2       thrpt       10  46658242.728   987901.581  ops/s
     * c.g.d.r.BenchmarksMbr.mbrList3       thrpt       10  40357809.306   937827.660  ops/s
     * c.g.d.r.BenchmarksMbr.mbrList4       thrpt       10  35930532.557   605535.237  ops/s
     * c.g.d.r.BenchmarksMbr.mbrOldList1    thrpt       10  55848118.198  1342997.309  ops/s
     * c.g.d.r.BenchmarksMbr.mbrOldList2    thrpt       10  25171873.903   395127.918  ops/s
     * c.g.d.r.BenchmarksMbr.mbrOldList3    thrpt       10  19222116.139   246965.178  ops/s
     * c.g.d.r.BenchmarksMbr.mbrOldList4    thrpt       10  14891862.638   198765.157  ops/s
     * </pre>
     * 
     * @param items
     * @return
     */
    public static Rectangle mbr(Collection<? extends HasGeometry> items) {
        Preconditions.checkArgument(!items.isEmpty());
        float minX1 = Float.MAX_VALUE;
        float minY1 = Float.MAX_VALUE;
        float maxX2 = Float.MIN_VALUE;
        float maxY2 = Float.MIN_VALUE;
        for (final HasGeometry item : items) {
            Rectangle r = item.geometry().mbr();
            if (r.x1() < minX1)
                minX1 = r.x1();
            if (r.y1() < minY1)
                minY1 = r.y1();
            if (r.x2() > maxX2)
                maxX2 = r.x2();
            if (r.y2() > maxY2)
                maxY2 = r.y2();
        }
        return Rectangle.create(minX1, minY1, maxX2, maxY2);
    }

    static <T> List<T> add(List<T> list, T element) {
        final ArrayList<T> result = new ArrayList<T>(list);
        result.add(element);
        return result;
    }

    static <T> List<T> remove(List<T> list, T element) {
        final ArrayList<T> result = new ArrayList<T>(list);
        result.remove(element);
        return result;
    }

    static <T> List<T> remove(List<? extends T> list, List<? extends T> elements) {
        final ArrayList<T> result = new ArrayList<T>(list);
        result.removeAll(elements);
        return result;
    }

    static <T> List<? extends T> replaceOld(List<? extends T> list, T node,
            List<? extends T> replacements) {
        final ArrayList<T> result = new ArrayList<T>(list);
        result.remove(node);
        result.addAll(replacements);
        return result;
    }

    static <T> List<? extends T> replace(List<? extends T> list, T element, List<T> replacements) {
        List<T> list2 = new ArrayList<T>();
        for (T node : list)
            if (node != element)
                list2.add(node);
        list2.addAll(replacements);
        return list2;
    }
}
