package com.isscroberto.dailyprayerandroid.data.source;

import com.isscroberto.dailyprayerandroid.data.models.Prayer;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by roberto.orozco on 21/09/2017.
 */

@Singleton
public class PrayerLocalDataSource  {

    private final Realm mRealm;

    @Inject
    public PrayerLocalDataSource() {
        mRealm = Realm.getDefaultInstance();
    }

    public RealmResults<Prayer> get () {
        return mRealm.where(Prayer.class).findAllSorted("Id", Sort.DESCENDING);
    }

    public Prayer get(String id) {
        return mRealm.where(Prayer.class).equalTo("Id", id).findFirst();
    }

    public Prayer put(Prayer prayer) {
        mRealm.beginTransaction();
        Prayer managedPrayer = mRealm.copyToRealm(prayer);
        mRealm.commitTransaction();
        return managedPrayer;
    }

    public void delete (String id) {
        final Prayer prayer = mRealm.where(Prayer.class).equalTo("Id", id).findFirst();
        if(prayer != null) {
            mRealm.executeTransaction(realm -> prayer.deleteFromRealm());
        }
    }

}
