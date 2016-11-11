package app.com.thetechnocafe.locationreader.MainLocationReader;

import java.util.List;

import app.com.thetechnocafe.locationreader.Common.RealmDatabase;

/**
 * Created by gurleensethi on 10/11/16.
 */

public class Presenter implements MVPContracts.IPresenter {

    private MVPContracts.IView mView;

    public Presenter(MVPContracts.IView view) {
        mView = view;
        mView.setUpView();
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

    @Override
    public void getLocationData() {
        List<LocationModel> list = RealmDatabase.getInstance(mView.getContext()).getSaveLocations();
        mView.onDataReceived(list);
    }
}
