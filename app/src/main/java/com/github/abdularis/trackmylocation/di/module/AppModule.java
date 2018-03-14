package com.github.abdularis.trackmylocation.di.module;

import android.app.Application;
import android.content.Context;

import com.github.abdularis.trackmylocation.ViewModelFactory;
import com.github.abdularis.trackmylocation.data.MyLocationDataServer;
import com.github.abdularis.trackmylocation.data.MyLocationProvider;
import com.github.abdularis.trackmylocation.locationbroadcast.LocationBroadcastViewModel;

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
    MyLocationProvider provideDeviceLocationProvider() {
        return new MyLocationProvider();
    }

    @Provides
    @Singleton
    MyLocationDataServer provideMyLocationDataServer() {
        return new MyLocationDataServer();
    }

    @Provides
    @Singleton
    LocationBroadcastViewModel provideLocationBroadcastViewModel(
            MyLocationProvider locationProvider, MyLocationDataServer locationDataServer) {
        return new LocationBroadcastViewModel(mApplication, locationProvider, locationDataServer);
    }

    @Provides
    @Singleton
    ViewModelFactory provideViewModelFactory(
            LocationBroadcastViewModel locationBroadcastViewModel) {
        return new ViewModelFactory(locationBroadcastViewModel);
    }

}
