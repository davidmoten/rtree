package com.github.davidmoten.rtree;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.zip.GZIPInputStream;

import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Point;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.observables.StringObservable;

public class GreekEarthquakes {

    public static Observable<Entry<Object, Point>> entries(final Precision precision) {
        Observable<String> source = Observable.using(new Func0<InputStream>() {
            @Override
            public InputStream call() {
                try {
                    return new GZIPInputStream(GreekEarthquakes.class
                            .getResourceAsStream("/greek-earthquakes-1964-2000.txt.gz"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }, new Func1<InputStream, Observable<String>>() {
            @Override
            public Observable<String> call(InputStream is) {
                return StringObservable.from(new InputStreamReader(is));
            }
        }, new Action1<InputStream>() {
            @Override
            public void call(InputStream is) {
                try {
                    is.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        return StringObservable.split(source, "\n")
                .flatMap(new Func1<String, Observable<Entry<Object, Point>>>() {

                    @Override
                    public Observable<Entry<Object, Point>> call(String line) {
                        if (line.trim().length() > 0) {
                            String[] items = line.split(" ");
                            double lat = Double.parseDouble(items[0]);
                            double lon = Double.parseDouble(items[1]);
                            Entry<Object, Point> entry;
                            if (precision == Precision.DOUBLE)
                                entry = Entries.entry(new Object(), Geometries.point(lat, lon));
                            else
                                entry = Entries.entry(new Object(),
                                        Geometries.point((float) lat, (float) lon));
                            return Observable.just(entry);
                        } else
                            return Observable.empty();
                    }
                });
    }

    static List<Entry<Object, Point>> entriesList(Precision precision) {
        List<Entry<Object, Point>> result = entries(precision).toList().toBlocking().single();
        System.out.println("loaded greek earthquakes into list");
        return result;
    }

    public static void main(String[] args) throws InterruptedException {
        RTree<Object, Point> tree = RTree.star().create();
        tree = tree.add(entries(Precision.SINGLE)).last().toBlocking().single();
        System.gc();
        Thread.sleep(10000000);
        System.out.println(tree.size());
    }
}
