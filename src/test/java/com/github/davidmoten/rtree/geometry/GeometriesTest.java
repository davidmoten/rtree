package com.github.davidmoten.rtree.geometry;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.junit.Test;

public class GeometriesTest {

    @Test
    public void testPrivateConstructorForCoverageOnly() throws InstantiationException,
            IllegalAccessException, NoSuchMethodException, SecurityException,
            IllegalArgumentException, InvocationTargetException {
        Constructor<Geometries> c = Geometries.class.getDeclaredConstructor();
        c.setAccessible(true);
        c.newInstance();
    }

}
