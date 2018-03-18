package com.github.abdularis.trackmylocation.tracklocation;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.github.abdularis.trackmylocation.data.DeviceLocationDataStore;
import com.github.abdularis.trackmylocation.model.SharedLocation;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;

public class TrackLocationViewModel extends ViewModel {

    private DeviceLocationDataStore mDeviceLocationDataStore;
    private MutableLiveData<Boolean> mTrackingState;
    private CompositeDisposable mCompositeDisposable;

    @Inject
    public TrackLocationViewModel(DeviceLocationDataStore deviceLocationDataStore) {
        mDeviceLocationDataStore = deviceLocationDataStore;
        mTrackingState = new MutableLiveData<>();
        mCompositeDisposable = new CompositeDisposable();
    }

    public void stopTracking() {
        if (isTracking()) {
            mTrackingState.setValue(false);
            mCompositeDisposable.dispose();
            mCompositeDisposable = new CompositeDisposable();
        }
    }

    public Observable<SharedLocation> getLocationUpdate(String devId) {
        return mDeviceLocationDataStore.getSharedLocationUpdate(devId)
                .doOnSubscribe(disposable -> {
                    mCompositeDisposable.add(disposable);
                    if (!isTracking()) {
                        mTrackingState.setValue(true);
                    }
                })
                .doOnError(throwable -> {
                    if (isTracking()) {
                        stopTracking();
                    }
                });
    }

    public MutableLiveData<Boolean> getTrackingState() {
        return mTrackingState;
    }

    public boolean isTracking() {
        return mTrackingState.getValue() != null && mTrackingState.getValue();
    }
}
