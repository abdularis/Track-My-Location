package com.github.abdularis.trackmylocation;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.github.abdularis.trackmylocation.locationbroadcast.LocationBroadcastViewModel;

import javax.inject.Inject;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private LocationBroadcastViewModel mLocationBroadcastViewModel;

    @Inject
    public ViewModelFactory(LocationBroadcastViewModel locationBroadcastViewModel) {
        mLocationBroadcastViewModel = locationBroadcastViewModel;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(LocationBroadcastViewModel.class)) {
            return (T) mLocationBroadcastViewModel;
        }

        throw new IllegalArgumentException("Unknown view model type");
    }
}
