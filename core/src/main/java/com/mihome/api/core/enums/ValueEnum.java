package com.mihome.api.core.enums;

import com.mihome.api.core.ApiException;

import java.util.stream.Stream;

public interface ValueEnum<T> {
    T getValue();


    static <T> Object find(String value, ValueEnum<T>[] values) {
        return Stream.of(values)
                .filter(a -> a.getValue().equals(value))
                .findFirst()
                .orElse(null);
    }

    static <T> Object findOrThrowException(String value, ValueEnum<T>[] values) {
        return Stream.of(values)
                .filter(a -> a.getValue().equals(value))
                .findFirst()
                .orElseThrow(() -> new ApiException("Unknown action: " + value));
    }

}
