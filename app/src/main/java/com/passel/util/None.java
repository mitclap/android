package com.passel.util;

/**
 * Created by aneesh on 4/19/15.
 */
public class None<T> extends Optional<T> {

    @Override
    public boolean isPresent() {
        return false;
    }
}
