package com.passel.util;

import java.util.NoSuchElementException;

/**
 * Created by aneesh on 4/19/15.
 */
public abstract class Optional<T> {

    public abstract boolean isPresent();

    public T get() throws NoSuchElementException {
        try {
            Some<T> someThis = (Some<T>) this;
            return someThis.get();
        } catch (ClassCastException e) {
            throw new NoSuchElementException();
        }
    }

    public static <T> Optional<T> of(T value) {
        return new Some<>(value);
    };

    public static <T> Optional<T> empty() {
        return new None<>();
    }
}
