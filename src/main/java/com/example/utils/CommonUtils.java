package com.example.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;

@NoArgsConstructor(access = AccessLevel.NONE)
public class CommonUtils {

    public static boolean hasContent(String s) {
        return s != null && !s.isBlank();
    }

    @SafeVarargs
    public static <T> Optional<T> findInCollection(Collection<T> items, T value, Function<T, ?>... propertyGetters) {
        for (T item : items) {
            if (compare(item, value, propertyGetters))
                return Optional.of(item);
        }

        return Optional.empty();
    }

    @SafeVarargs
    public static <T> boolean contains(Collection<T> items, T value, Function<T, ?>... propertyGetters) {
        for (T item : items) {
            if (compare(item, value, propertyGetters))
                return true;
        }

        return false;
    }

    @SafeVarargs
    public static <T> boolean compare(T o1, T o2, Function<T, ?>... propertyGetters) {
        if (o1 == null && o2 == null)
            return true;

        if (o1 == null || o2 == null)
            return false;

        for (Function<T, ?> getter : propertyGetters) {
            Object itemField = getter.apply(o1);
            Object valueField = getter.apply(o2);

            if (itemField == null && valueField == null)
                continue;

            if (itemField == null || valueField == null)
                return false;

            if (itemField.equals(valueField))
                continue;

            return false;
        }

        return true;
    }

}
