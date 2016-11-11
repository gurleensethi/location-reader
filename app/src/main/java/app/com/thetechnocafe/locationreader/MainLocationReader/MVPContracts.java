package app.com.thetechnocafe.locationreader.MainLocationReader;

import android.content.Context;

import java.util.List;

/**
 * Created by gurleensethi on 10/11/16.
 */

public class MVPContracts {
    //View Contract
    public interface IView {
        void onLocationAdded(Boolean isSuccessful);

        Context getContext();

        void setUpView();

        void onDataReceived(List<LocationModel> list);
    }

    //Presenter Contract
    public interface IPresenter {
        void addLocation(String string, String latitude, String longitude);

        void getLocationData();
    }
}
