package com.passel.api.messaging;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

import lombok.EqualsAndHashCode;
import lombok.Value;

// TODO: integrate with the data NewEvent and Event types :)

@Value
// No need to call super implementation of equals and hashcode; explicitly let Lombok know this is OK
@EqualsAndHashCode(callSuper = false)
public final class NewEventMessage implements Message {

    private final String name;
    private final Date start;
    private final Date end;
    private final String description;

    @JsonCreator
    public NewEventMessage(@JsonProperty("name") String name,
                           @JsonProperty("start") Date start,
                           @JsonProperty("end") Date end,
                           @JsonProperty("description") String description) {
        this.name = name;
        this.start = start;
        this.end = end;
        this.description = description;
    }

    @Override
    @JsonIgnore
    public String getEndpoint() {
        return "/events";
    }
}
