package com.passel.api;

import lombok.Value;

/**
 * Created by aneesh on 4/18/15.
 */
@Value
public class APIResponse {
    private int code;
    private String body;

    public APIResponse(int code, String body) {
        this.code = code;
        this.body = body;
    }
}