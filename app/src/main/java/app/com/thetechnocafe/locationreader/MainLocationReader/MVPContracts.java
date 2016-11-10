package app.com.thetechnocafe.locationreader.MainLocationReader;

import android.location.Location;

/**
 * Created by gurleensethi on 10/11/16.
 */

public class MVPContracts {
    //View Contract
    public interface IView {

    }

    //Presenter Contract
    public interface IPresenter {
        public void addLocation(String string, Location location);
    }
}
