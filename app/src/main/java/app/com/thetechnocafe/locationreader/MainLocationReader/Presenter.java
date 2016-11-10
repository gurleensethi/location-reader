package app.com.thetechnocafe.locationreader.MainLocationReader;

import android.location.Location;

import app.com.thetechnocafe.locationreader.Common.RealmDatabase;

/**
 * Created by gurleensethi on 10/11/16.
 */

public class Presenter implements MVPContracts.IPresenter {
    @Override
    public void addLocation(String string, Location location) {
        //Create location model
        LocationModel model = new LocationModel();

        //Set the data
        model.setLocationName(string);
        model.setLatitude(location.getLatitude());
        model.setLongitude(location.getLongitude());

        //Insert into realm
        RealmDatabase.getInstance()
                .insertLocation(model);
    }
}
