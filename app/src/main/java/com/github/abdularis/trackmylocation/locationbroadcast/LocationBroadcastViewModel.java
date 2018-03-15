package com.github.abdularis.trackmylocation.locationbroadcast;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.location.Location;

import com.github.abdularis.trackmylocation.data.MyLocationDataServer;
import com.github.abdularis.trackmylocation.data.MyLocationProvider;

import javax.inject.Inject;

import io.reactivex.Notification;
import io.reactivex.Observable;

public class LocationBroadcastViewModel extends AndroidViewModel {

    private MyLocationProvider mLocationProvider;
    private MyLocationDataServer mLocationDataServer;
    private MutableLiveData<Boolean> mIsBroadcasting;
    private Location mLastLocation;

    @Inject
    public LocationBroadcastViewModel(Application application,
                                      MyLocationProvider locationProvider,
                                      MyLocationDataServer locationDataServer) {
        super(application);
        mLocationProvider = locationProvider;
        mLocationDataServer = locationDataServer;
        mIsBroadcasting = new MutableLiveData<>();
    }

    public void switchBroadcast() {
        if (isBroadcasting()) {
            mIsBroadcasting.setValue(false);
            mLocationDataServer.clearCurrentLocation();
        } else {
            mIsBroadcasting.setValue(true);
        }
    }

    public void connect() {
        mLocationProvider.connectService(getApplication());
    }

    public void disconnect() {
        mLocationProvider.disconnectService();
    }

    public Observable<Integer> getLocationProviderConnection() {
        return mLocationProvider.getConnectionObservable();
    }

    public Observable<Notification<Location>> getLocation() {
        return mLocationProvider.getLocationObservable()
                .doOnNext(locationNotification -> {
                    if (locationNotification.isOnNext()) {
                        mLastLocation = locationNotification.getValue();
                        if (isBroadcasting()) {
                            mLocationDataServer.setCurrentLocation(mLastLocation);
                        }
                    }
                });
    }

    public MutableLiveData<Boolean> getIsBroadcastingObservable() {
        return mIsBroadcasting;
    }

    public boolean isBroadcasting() {
        return mIsBroadcasting.getValue() != null && mIsBroadcasting.getValue();
    }

    public Observable<String> getDeviceIdObservable() {
        return mLocationDataServer.getDevIdObservable();
    }

    public Location getLastLocation() {
        return mLastLocation;
    }
}
