package com.passel.api;

/**
 * Created by aneesh on 4/18/15.
 */
public class APIError extends Exception {
    public APIError(Throwable throwable) {
        super(throwable);
    }
    public APIError(String message) {
        super(message);
    }
}
