package com.passel.api.messaging;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

/**
 * Created by aneesh on 5/5/15.
 */
@Value
public class ResponseMessage<T> {
    private String message;
    private T data;

    @JsonCreator
    public ResponseMessage(@JsonProperty("message") final String message,
                           @JsonProperty("data") final T data) {
        this.message = message;
        this.data = data;
    }
}

