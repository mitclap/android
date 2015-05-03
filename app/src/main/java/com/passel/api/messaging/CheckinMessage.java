package com.passel.api.messaging;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.passel.data.Location;

import java.util.Date;

import lombok.EqualsAndHashCode;
import lombok.Value;

// TODO: integrate with the data NewEvent and Event types :)

@Value
// No need to call super implementation of equals and hashcode; explicitly let Lombok know this is OK
@EqualsAndHashCode(callSuper = false)
public final class CheckinMessage implements Message {

    private final int eventId;
    private final int accountId;
    private final Date timestamp;
    private final Location location;

    @JsonCreator
    public CheckinMessage(@JsonProperty("eventId") final int eventId,
                          @JsonProperty("accountId") final int accountId,
                          @JsonProperty("timestamp") final Date timestamp,
                          @JsonProperty("location") final Location location) {
        this.eventId = eventId;
        this.accountId = accountId;
        this.timestamp = timestamp;
        this.location = location;
    }

    @Override
    @JsonIgnore
    public String getEndpoint() {
        return "/checkins";
    }
}
