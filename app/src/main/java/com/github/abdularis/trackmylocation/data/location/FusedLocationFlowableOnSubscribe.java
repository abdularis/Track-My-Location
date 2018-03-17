package com.github.abdularis.trackmylocation.data.location;

import android.content.Context;
import android.location.Location;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import io.reactivex.FlowableEmitter;

public class FusedLocationFlowableOnSubscribe extends GoogleApiClientFlowableOnSubscribe<Location> {

    private LocationUpdateListener mLocationUpdateListener;
    private LocationRequest mLocationRequest;

    public FusedLocationFlowableOnSubscribe(Context context, LocationRequest locationRequest) {
        super(context);
        mLocationRequest = locationRequest;
    }

    @Override
    protected void onGoogleApiClientReady(GoogleApiClient apiClient, FlowableEmitter<Location> emitter) {
        mLocationUpdateListener = new LocationUpdateListener(emitter);

        try {
            LocationServices.FusedLocationApi
                    .requestLocationUpdates(apiClient, mLocationRequest, mLocationUpdateListener);
        } catch (SecurityException e) {
            emitter.onError(e);
        }
    }

    @Override
    protected void onUnsubscribe(GoogleApiClient apiClient) {
        LocationServices.FusedLocationApi.removeLocationUpdates(apiClient, mLocationUpdateListener);
        mLocationUpdateListener.emitter = null;
        mLocationUpdateListener = null;
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
