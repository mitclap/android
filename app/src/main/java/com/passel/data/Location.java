package com.passel.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

/**
 * Used instead of Android's location class for efficiency.
 * We make a lot of these and send them all over so we want less overhead.
 * Also, we'll probably add some of those features back but make them optional.
 */
@Value
public final class Location implements Parcelable {
    public static final Parcelable.Creator<Location> CREATOR
            = new Parcelable.Creator<Location>() {
        public Location createFromParcel(Parcel in) {
            final double latitude = in.readDouble();
            final double longitude = in.readDouble();
            return new Location(latitude, longitude);
        }

        @Override
        public Location[] newArray(final int size) {
            return new Location[size];
        }
    };

    double latitude;
    double longitude;

    @JsonCreator
    public Location(@JsonProperty("latitude") final double latitude,
                    @JsonProperty("longtitude") final double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Location(final android.location.Location location) {
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
    }

    @Override
    public String toString() {
        return Double.toString(latitude) + "," + Double.toString(longitude);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
    }
}
