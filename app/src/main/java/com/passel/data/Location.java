package com.passel.data;

import hrisey.Parcelable;
import lombok.Value;

/**
 * Created by aneesh on 4/19/15.
 */
@Value
@Parcelable
public final class Location implements android.os.Parcelable {
    double latitude;
    double longitude;

    public Location(final double latitude, final double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return Double.toString(latitude) + "," + Double.toString(longitude);
    }
}
