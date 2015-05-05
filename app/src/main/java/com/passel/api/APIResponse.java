package com.passel.api;

import com.passel.api.messaging.ResponseMessage;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Value;

/**
 * Created by aneesh on 4/18/15.
 */
@Value
public class APIResponse<T> {
    private int code;
    @Getter(AccessLevel.NONE) private ResponseMessage<T> response;

    public APIResponse(final int code, final ResponseMessage<T> response) {
        this.code = code;
        this.response = response;
    }

    public String getMessage() {
        return response.getMessage();
    }

    public T getData() {
        return response.getData();
    }
}
