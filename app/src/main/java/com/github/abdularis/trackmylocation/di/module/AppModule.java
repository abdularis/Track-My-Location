package com.github.abdularis.trackmylocation.di.module;

import android.app.Application;
import android.content.Context;

import com.github.abdularis.trackmylocation.ViewModelFactory;
import com.github.abdularis.trackmylocation.data.LocationTrackerDataClient;
import com.github.abdularis.trackmylocation.data.MyLocationDataServer;
import com.github.abdularis.trackmylocation.sharelocation.ShareLocationViewModel;
import com.github.abdularis.trackmylocation.tracklocation.TrackLocationViewModel;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    private Application mApplication;

    public AppModule(Application app) {
        mApplication = app;
    }

    @Provides
    @Singleton
    Context provideContext() {
        return mApplication;
    }

    @Provides
    @Singleton
    MyLocationDataServer provideMyLocationDataServer() {
        return new MyLocationDataServer();
    }

    @Provides
    @Singleton
    ShareLocationViewModel provideShareLocationViewModel(MyLocationDataServer locationDataServer) {
        return new ShareLocationViewModel(mApplication, locationDataServer);
    }

    @Provides
    @Singleton
    TrackLocationViewModel provideTrackLocationViewModel() {
        return new TrackLocationViewModel(new LocationTrackerDataClient());
    }

    @Provides
    @Singleton
    ViewModelFactory provideViewModelFactory(
            ShareLocationViewModel shareLocationViewModel,
            TrackLocationViewModel trackLocationViewModel) {
        return new ViewModelFactory(shareLocationViewModel, trackLocationViewModel);
    }

}
