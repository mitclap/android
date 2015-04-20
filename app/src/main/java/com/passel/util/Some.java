package com.passel.util;

/**
 * Created by aneesh on 4/19/15.
 */
public class Some<T> extends Optional<T> {

    private T value;

    public Some(T value) {
        this.value = value;
    }

    @Override
    public boolean isPresent() {
        return true;
    }

    @Override
    public T get() {
        return value;
    }
}
