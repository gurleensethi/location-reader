package app.com.thetechnocafe.locationreader.MainLocationReader;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by gurleensethi on 10/11/16.
 */

public class LocationModel extends RealmObject {
    @PrimaryKey
    private String mLocationName;
    private Double mLatitude;
    private Double mLongitude;

    public Double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(Double latitude) {
        mLatitude = latitude;
    }

    public String getLocationName() {
        return mLocationName;
    }

    public void setLocationName(String locationName) {
        mLocationName = locationName;
    }

    public Double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(Double longitude) {
        mLongitude = longitude;
    }
}
