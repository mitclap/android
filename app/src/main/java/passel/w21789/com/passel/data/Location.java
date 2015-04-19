package passel.w21789.com.passel.data;

import lombok.Value;

/**
 * Created by aneesh on 4/19/15.
 */
@Value
public class Location {
    private double latitude;
    private double longitude;

    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return Double.toString(latitude) + "," + Double.toString(longitude);
    }
}
