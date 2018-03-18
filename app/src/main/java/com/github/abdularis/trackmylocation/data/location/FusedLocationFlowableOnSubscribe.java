package com.github.abdularis.trackmylocation.data.location;

import android.content.Context;
import android.location.Location;
import android.util.SparseArray;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;


import io.reactivex.FlowableEmitter;

public class FusedLocationFlowableOnSubscribe extends GoogleApiClientFlowableOnSubscribe<Location> {

    private LocationRequest mLocationRequest;
    private SparseArray<LocationUpdateListener> mLocationUpdateListeners;

    public FusedLocationFlowableOnSubscribe(Context context, LocationRequest locationRequest) {
        super(context);
        mLocationRequest = locationRequest;
        mLocationUpdateListeners = new SparseArray<>();
    }

    @Override
    protected void onGoogleApiClientReady(GoogleApiClient apiClient, FlowableEmitter<Location> emitter) {
        LocationUpdateListener locationUpdateListener = new LocationUpdateListener(emitter);
        try {
            LocationServices.FusedLocationApi
                    .requestLocationUpdates(apiClient, mLocationRequest, locationUpdateListener);
            mLocationUpdateListeners.put(emitter.hashCode(), locationUpdateListener);
        } catch (SecurityException e) {
            emitter.onError(e);
        }
    }

    @Override
    protected void onEmitterUnsubscribe(GoogleApiClient apiClient, FlowableEmitter<Location> emitter) {
        int key = emitter.hashCode();
        LocationUpdateListener locationUpdateListener = mLocationUpdateListeners.get(key);
        mLocationUpdateListeners.remove(key);
        if (locationUpdateListener != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(apiClient, locationUpdateListener);
            locationUpdateListener.emitter = null;
        }
    }

    protected class LocationUpdateListener implements LocationListener {

        FlowableEmitter<Location> emitter;

        LocationUpdateListener(FlowableEmitter<Location> emitter) {
            this.emitter = emitter;
        }

        @Override
        public void onLocationChanged(Location location) {
            emitter.onNext(location);
        }
    }
}
