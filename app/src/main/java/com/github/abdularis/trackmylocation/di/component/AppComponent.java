package com.github.abdularis.trackmylocation.di.component;

import com.github.abdularis.trackmylocation.di.module.AppModule;
import com.github.abdularis.trackmylocation.locationbroadcast.LocationBroadcastActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {

    void inject(LocationBroadcastActivity client);

}
