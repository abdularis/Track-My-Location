package com.github.abdularis.trackmylocation.data;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import io.reactivex.Notification;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;

public class MyLocationProvider implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public static final int CONNECTION_CONNECTED = 1;
    public static final int CONNECTION_SUSPENDED = 2;
    public static final int CONNECTION_FAILED = 3;

    private GoogleApiClient mApiClient;
    private LocationRequest mLocationRequest;
    private BehaviorSubject<Notification<Location>> mLocationSubject;
    private PublishSubject<Integer> mConnectionSubject;

    public MyLocationProvider() {
        mLocationSubject = BehaviorSubject.create();
        mConnectionSubject = PublishSubject.create();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mConnectionSubject.onNext(CONNECTION_CONNECTED);

        try {
            LocationServices
                    .FusedLocationApi
                    .requestLocationUpdates(mApiClient, mLocationRequest, this);
        } catch (SecurityException e) {
            mLocationSubject.onNext(Notification.<Location>createOnError(e));
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mConnectionSubject.onNext(CONNECTION_SUSPENDED);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        mConnectionSubject.onNext(CONNECTION_FAILED);
    }

    @Override
    public void onLocationChanged(Location location) {
        mLocationSubject.onNext(Notification.createOnNext(location));
    }

    public Observable<Notification<Location>> getLocationObservable() {
        return mLocationSubject;
    }

    public Observable<Integer> getConnectionObservable() {
        return mConnectionSubject;
    }

    public void connectService(Context context) {
        if (mApiClient == null) {
            mApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            mApiClient.connect();

            mLocationRequest = LocationRequest.create();
            mLocationRequest.setInterval(1500);
            mLocationRequest.setFastestInterval(2000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        } else {
            mApiClient.connect();
        }
    }

    public void disconnectService() {
        if (mApiClient != null) {
            if (mApiClient.isConnected()) mApiClient.disconnect();
            mApiClient = null;
            mLocationRequest = null;
        }
    }
}
