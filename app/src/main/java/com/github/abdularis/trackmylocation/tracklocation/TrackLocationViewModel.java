package com.github.abdularis.trackmylocation.tracklocation;

import android.arch.lifecycle.ViewModel;
import android.location.Location;

import com.github.abdularis.trackmylocation.data.LocationTrackerDataClient;
import com.github.abdularis.trackmylocation.model.TrackedLocation;

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

    public Observable<TrackedLocation> getTrackedLocationUpdate() {
        return mLocationTracker.getLocationUpdate();
    }

}
