package com.passel.util;

/**
 * Created by aneesh on 4/18/15.
 */
public abstract class Result<T, E> {
    public abstract boolean isOk();

    public abstract boolean isErr();

    public T unwrap() throws ClassCastException {
        return ((Ok<T, E>) this).get();
    }
}
