package com.passel.data;

import java.util.Date;
import java.util.List;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.FieldDefaults;


/**
 * Exists mostly for code reuse.
 */
@EqualsAndHashCode
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Getter
public abstract class Event {
    int localId;
    String name;
    Date start;
    Date end;
    String description;
    List<String> attendees;
    Location location;

    protected Event(final int localId,
                    final String name,
                    final Date start,
                    final Date end,
                    final String description,
                    final List<String> attendees,
                    final Location location) {
        this.localId = localId;
        this.name = name;
        this.start = start;
        this.end = end;
        this.description = description;
        this.attendees = attendees;
        this.location = location;
    }

}