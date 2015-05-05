package com.passel.api.messaging;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

/**
 * Created by aneesh on 5/5/15.
 */
@Value
public class CheckinCreatedMessage {
    int checkinId;

    public CheckinCreatedMessage(@JsonProperty("checkinId") int checkinId) {
        this.checkinId = checkinId;
    }
}
