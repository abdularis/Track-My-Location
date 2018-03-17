package com.github.abdularis.trackmylocation.di.component;

import com.github.abdularis.trackmylocation.di.module.AppModule;
import com.github.abdularis.trackmylocation.sharelocation.ShareLocationActivity;
import com.github.abdularis.trackmylocation.tracklocation.TrackLocationActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {

    void inject(ShareLocationActivity client);

    void inject(TrackLocationActivity client);

}
