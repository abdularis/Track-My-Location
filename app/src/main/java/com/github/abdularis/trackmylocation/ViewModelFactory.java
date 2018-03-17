package com.github.abdularis.trackmylocation;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.github.abdularis.trackmylocation.sharelocation.ShareLocationViewModel;
import com.github.abdularis.trackmylocation.tracklocation.TrackLocationViewModel;

import javax.inject.Inject;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private ShareLocationViewModel mShareLocationViewModel;
    private TrackLocationViewModel mTrackLocationViewModel;

    @Inject
    public ViewModelFactory(ShareLocationViewModel shareLocationViewModel,
                            TrackLocationViewModel trackLocationViewModel) {
        mShareLocationViewModel = shareLocationViewModel;
        mTrackLocationViewModel = trackLocationViewModel;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ShareLocationViewModel.class)) {
            return (T) mShareLocationViewModel;
        } else if (modelClass.isAssignableFrom(TrackLocationViewModel.class)) {
            return (T) mTrackLocationViewModel;
        }

        throw new IllegalArgumentException("Unknown view model type");
    }
}
