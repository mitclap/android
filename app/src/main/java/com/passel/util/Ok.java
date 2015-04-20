package com.passel.util;

/**
 * Created by aneesh on 4/18/15.
 */
public class Ok<T, E> extends Result<T, E> {
    private T value;
    public Ok(T value) {
        this.value = value;
    }

    public T get() {
        return value;
    }

    @Override
    public boolean isOk() {
        return true;
    }

    @Override
    public boolean isErr() {
        return false;
    }
}
