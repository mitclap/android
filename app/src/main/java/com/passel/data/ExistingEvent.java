package com.passel.data;

import java.util.Date;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
public final class ExistingEvent extends Event {
    int id;

    public ExistingEvent(int id,
                         final String name,
                         final Date start,
                         final Date end,
                         final String description,
                         final List<String> guests,
                         final Location location) {
        super(name, start, end, description, guests, location);
        this.id = id;
    }
}