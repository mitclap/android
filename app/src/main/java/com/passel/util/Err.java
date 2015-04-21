package com.passel.util;

/**
 * Created by aneesh on 4/18/15.
 */
public class Err<T, E> extends Result<T, E> {
    private final E error;

    public Err(final E error) {
        this.error = error;
    }

    public E get() {
        return error;
    }

    @Override
    public boolean isOk() {
        return false;
    }

    @Override
    public boolean isErr() {
        return true;
    }
}
