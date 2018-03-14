package com.github.abdularis.trackmylocation;

import android.app.Application;

import com.github.abdularis.trackmylocation.di.component.AppComponent;
import com.github.abdularis.trackmylocation.di.component.DaggerAppComponent;
import com.github.abdularis.trackmylocation.di.module.AppModule;

public class App extends Application {

    private AppComponent mAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        AppModule appModule = new AppModule(this);
        mAppComponent = DaggerAppComponent.builder().appModule(appModule).build();
    }

    public AppComponent getAppComponent() {
        return mAppComponent;
    }
}
