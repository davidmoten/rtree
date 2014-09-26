package com.github.davidmoten.util;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

/**
 * Utility methods for unit tests.
 * 
 * @author dave
 * 
 */
public class TestingUtil {

    /**
     * Checks that a class has a no-argument private constructor and calls that
     * constructor to instantiate the class.
     * 
     * @param cls
     */
    public static <T> void callConstructorAndCheckIsPrivate(Class<T> cls) {
        Constructor<T> constructor;
        try {
            constructor = cls.getDeclaredConstructor();
        } catch (NoSuchMethodException e1) {
            throw new RuntimeException(e1);
        } catch (SecurityException e1) {
            throw new RuntimeException(e1);
        }
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        try {
            constructor.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

}