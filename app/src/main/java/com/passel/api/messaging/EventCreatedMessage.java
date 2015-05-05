package com.passel.api.messaging;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

/**
 * Created by aneesh on 5/5/15.
 */
@Value
public class EventCreatedMessage {
    int eventId;

    public EventCreatedMessage(@JsonProperty("eventId") int eventId) {
        this.eventId = eventId;
    }
}
