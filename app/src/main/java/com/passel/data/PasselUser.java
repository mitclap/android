package com.passel.data;

import java.util.Date;
import java.util.List;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.Data;

/**
 * Simple class to hold basic information about our users
 * TODO: Talk to @aneeshusa on getting this onto the server/changing spec if necessary
 *
 * Created by Carlos Henriquez on 4/28/2015.
 */
@Data
@EqualsAndHashCode
@Getter
public class PasselUser {
    final String username;
    @Setter boolean venmoEnabled = false;
    @Setter boolean GPSEnabled = false; //TODO: default to true??
    @Setter Location location; //TODO: Create sentinel location?
    @Setter boolean attendingEvent = false;

    protected PasselUser(final String username){
        this.username = username;
        this.venmoEnabled = venmoEnabled;
        this.GPSEnabled = GPSEnabled;
        this.attendingEvent = attendingEvent;
        this.location = null; //TODO: CHANGE THIS BECAUSE NULL MAKES ME SAD
    }

}
