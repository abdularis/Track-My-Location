package com.github.abdularis.trackmylocation.data.location;

import android.content.Context;
import android.location.Location;
import android.support.annotation.NonNull;

import com.google.android.gms.location.LocationRequest;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;

public class RxLocation {

    public static Flowable<Location> getLocationUpdates(@NonNull Context context, LocationRequest locationRequest) {
        return Flowable
                .create(new FusedLocationFlowableOnSubscribe(context, locationRequest), BackpressureStrategy.MISSING);
    }

    public static Flowable<Location> getLocationUpdates(@NonNull Context context, long interval) {
        return getLocationUpdates(context, interval, 800);
    }

    public static Flowable<Location> getLocationUpdates(@NonNull Context context, long interval, long fastestInterval) {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(interval);
        locationRequest.setFastestInterval(fastestInterval);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return getLocationUpdates(context, locationRequest);
    }

}
