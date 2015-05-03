package com.passel.data;

import java.util.Date;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
final class NewEvent extends Event {

    NewEvent(final int localId,
             final String name,
             final Date start,
             final Date end,
             final String description,
             final List<String> attendees,
             final Location location) {
        super(localId, name, start, end, description, attendees, location);
    }
}