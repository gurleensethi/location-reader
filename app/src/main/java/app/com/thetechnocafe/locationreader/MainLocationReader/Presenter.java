package app.com.thetechnocafe.locationreader.MainLocationReader;

import app.com.thetechnocafe.locationreader.Common.RealmDatabase;

/**
 * Created by gurleensethi on 10/11/16.
 */

public class Presenter implements MVPContracts.IPresenter {

    private MVPContracts.IView mView;

    public Presenter(MVPContracts.IView view) {
        mView = view;
    }

    @Override
    public void addLocation(String string, String latitude, String longitude) {
        //Create location model
        LocationModel model = new LocationModel();

        //Set the data
        model.setLocationName(string);
        model.setLatitude(Double.parseDouble(latitude));
        model.setLongitude(Double.parseDouble(longitude));

        //Insert into realm
        mView.onLocationAdded(
                RealmDatabase.getInstance(mView.getContext())
                        .insertLocation(model)
        );
    }
}
