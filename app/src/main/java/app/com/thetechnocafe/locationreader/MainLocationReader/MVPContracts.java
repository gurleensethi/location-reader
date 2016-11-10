package app.com.thetechnocafe.locationreader.MainLocationReader;

import android.content.Context;

/**
 * Created by gurleensethi on 10/11/16.
 */

public class MVPContracts {
    //View Contract
    public interface IView {
        public void onLocationAdded(Boolean isSuccessful);

        public Context getContext();
    }

    //Presenter Contract
    public interface IPresenter {
        public void addLocation(String string, String latitude, String longitude);
    }
}
