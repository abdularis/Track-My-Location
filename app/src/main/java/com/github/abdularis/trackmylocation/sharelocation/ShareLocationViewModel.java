package com.github.abdularis.trackmylocation.sharelocation;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.location.Location;

import com.github.abdularis.trackmylocation.data.MyLocationDataServer;
import com.github.abdularis.trackmylocation.data.location.RxLocation;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.Observable;

public class ShareLocationViewModel extends AndroidViewModel {

    private MyLocationDataServer mLocationDataServer;
    private MutableLiveData<Boolean> mSharingState;
    private Location mLastLocation;
    private Flowable<Location> mLocationUpdatesObserver;

    @Inject
    public ShareLocationViewModel(Application application,
                                  MyLocationDataServer locationDataServer) {
        super(application);
        mLocationDataServer = locationDataServer;
        mSharingState = new MutableLiveData<>();
    }

    public void switchBroadcast() {
        if (isSharing()) {
            mSharingState.setValue(false);
            mLocationDataServer.clearCurrentLocation();
        } else {
            mSharingState.setValue(true);
        }
    }

    public Flowable<Location> getLocationUpdates() {
        if (mLocationUpdatesObserver != null) return mLocationUpdatesObserver;

        mLocationUpdatesObserver =
                RxLocation.getLocationUpdates(getApplication().getApplicationContext(), 1000)
                        .doOnNext(location -> {
                            mLastLocation = location;
                            if (isSharing()) {
                                mLocationDataServer.setCurrentLocation(mLastLocation);
                            }
                        });
        return mLocationUpdatesObserver;
    }

    public MutableLiveData<Boolean> getSharingStateLiveData() {
        return mSharingState;
    }

    public boolean isSharing() {
        return mSharingState.getValue() != null && mSharingState.getValue();
    }

    public Observable<String> getDeviceIdObservable() {
        return mLocationDataServer.getDevIdObservable();
    }

    public Location getLastCachedLocation() {
        return mLastLocation;
    }
}
