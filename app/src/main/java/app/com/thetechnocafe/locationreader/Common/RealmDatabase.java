package app.com.thetechnocafe.locationreader.Common;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import app.com.thetechnocafe.locationreader.MainLocationReader.LocationModel;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.exceptions.RealmPrimaryKeyConstraintException;

/**
 * Created by gurleensethi on 10/11/16.
 */

public class RealmDatabase {
    private static RealmDatabase mRealmDatabase;
    private final Realm mRealm;

    //Private constructor for singleton class
    private RealmDatabase(Context context) {
        //Initialize Realm
        Realm.init(context);
        mRealm = Realm.getDefaultInstance();
    }

    public static RealmDatabase getInstance(Context context) {
        if (mRealmDatabase == null) {
            mRealmDatabase = new RealmDatabase(context);
        }
        return mRealmDatabase;
    }

    //Return realm object
    public Realm getRealm() {
        return mRealm;
    }

    //Insert location
    public boolean insertLocation(final LocationModel locationModel) {
        try {
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.copyToRealm(locationModel);
                }
            });
        } catch (RealmPrimaryKeyConstraintException e) {
            return false;
        }

        return true;
    }

    //Clear the database
    public void clearAll() {
        mRealm.deleteAll();
    }

    //Get the list of all locations
    public List<LocationModel> getSaveLocations() {
        //Create a list of models
        List<LocationModel> list = new ArrayList<>();

        //Get the list of all locations
        RealmResults realmResults = mRealm.where(LocationModel.class).findAll();

        for (int count = 0; count < realmResults.size(); count++) {
            list.add((LocationModel) realmResults.get(count));
        }

        return list;
    }

    //Delete a particular item from database
    public void deleteLocaion(final String locationName) {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                mRealm.where(LocationModel.class).equalTo("mLocationName", locationName).findFirst().deleteFromRealm();
            }
        });
    }
}
