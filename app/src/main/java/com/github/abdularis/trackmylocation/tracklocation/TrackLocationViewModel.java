package com.github.abdularis.trackmylocation.tracklocation;

import android.arch.lifecycle.ViewModel;

import com.github.abdularis.trackmylocation.data.LocationTrackerDataClient;
import com.github.abdularis.trackmylocation.model.SharedLocation;

import javax.inject.Inject;

import io.reactivex.Observable;

public class TrackLocationViewModel extends ViewModel {

    private LocationTrackerDataClient mLocationTracker;

    @Inject
    public TrackLocationViewModel(LocationTrackerDataClient locationTracker) {
        mLocationTracker = locationTracker;
    }

    public void startTracking(String devId) {
        mLocationTracker.startTracking(devId);
    }

    public void stopTracking() {
        mLocationTracker.stopTracking();
    }

    public Observable<SharedLocation> getTrackedLocationUpdate() {
        return mLocationTracker.getLocationUpdate();
    }

}
