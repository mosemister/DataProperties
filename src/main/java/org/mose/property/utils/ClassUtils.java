package org.mose.property.utils;

public class ClassUtils {

    public static Class<?> fromPrimitive(Class<?> clazz) {
        if (!clazz.isPrimitive()) {
            return clazz;
        }
        if (int.class.equals(clazz)) {
            return Integer.class;
        }
        if (double.class.equals(clazz)) {
            return Double.class;
        }
        if (float.class.equals(clazz)) {
            return Float.class;
        }
        if (long.class.equals(clazz)) {
            return Long.class;
        }
        if (short.class.equals(clazz)) {
            return Short.class;
        }
        if (byte.class.equals(clazz)) {
            return Byte.class;
        }
        if (boolean.class.equals(clazz)) {
            return Boolean.class;
        }
        throw new RuntimeException("Cannot find primitive type for " + clazz.getName());
    }
}
