package com.github.abdularis.trackmylocation.data.location;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.github.abdularis.trackmylocation.data.location.errors.GoogleApiClientConnectionFailed;
import com.github.abdularis.trackmylocation.data.location.errors.GoogleApiClientConnectionSuspended;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;

public abstract class GoogleApiClientFlowableOnSubscribe<T> implements FlowableOnSubscribe<T> {

    private static final String TAG = "RxGoogleApiClient";

    private final Context mContext;
    private GoogleApiClient googleApiClient;
    private int googleApiClientUsageCount;

    public GoogleApiClientFlowableOnSubscribe(Context context) {
        mContext = context.getApplicationContext();
        googleApiClientUsageCount = 0;
    }

    @Override
    public void subscribe(FlowableEmitter<T> e) throws Exception {
        GoogleApiClientConnectionCallback apiClientCallback
                = new GoogleApiClientConnectionCallback(e);

        if (googleApiClient != null) {
            googleApiClient.registerConnectionCallbacks(apiClientCallback);
            googleApiClient.registerConnectionFailedListener(apiClientCallback);

            if (!googleApiClient.isConnected()) {
                googleApiClient.connect();
                Log.d(TAG, "Subscribe:Reconnect google api client");
            } else {
                Log.d(TAG, "Subscribe:Reuse google api client");
            }

        } else {
            Log.d(TAG, "Subscribe:Create new google api client instance");

            googleApiClient = new GoogleApiClient.Builder(mContext)
                    .addConnectionCallbacks(apiClientCallback)
                    .addOnConnectionFailedListener(apiClientCallback)
                    .addApi(LocationServices.API)
                    .build();
            googleApiClient.connect();
        }

        googleApiClientUsageCount++;
        e.setCancellable(() -> {
            if (googleApiClient != null && googleApiClient.isConnected()) {
                onEmitterUnsubscribe(googleApiClient, apiClientCallback.emitter);
                googleApiClient.unregisterConnectionCallbacks(apiClientCallback);
                googleApiClient.unregisterConnectionFailedListener(apiClientCallback);
                apiClientCallback.emitter = null;
                googleApiClientUsageCount--;
                if (googleApiClientUsageCount <= 0) {
                    Log.d(TAG, "Disconnect google api client, cause no one using it");
                    googleApiClient.disconnect();
                }
            }
        });
    }

    protected abstract void onGoogleApiClientReady(GoogleApiClient apiClient, FlowableEmitter<T> e);

    protected abstract void onEmitterUnsubscribe(GoogleApiClient apiClient, FlowableEmitter<T> e);

    protected class GoogleApiClientConnectionCallback
            implements GoogleApiClient.ConnectionCallbacks,
            GoogleApiClient.OnConnectionFailedListener {

        FlowableEmitter<T> emitter;

        GoogleApiClientConnectionCallback(FlowableEmitter<T> emitter) {
            this.emitter = emitter;
        }

        @Override
        public void onConnected(@Nullable Bundle bundle) {
            onGoogleApiClientReady(googleApiClient, emitter);
        }

        @Override
        public void onConnectionSuspended(int cause) {
            emitter.onError(new GoogleApiClientConnectionSuspended(cause));
        }

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            emitter.onError(new GoogleApiClientConnectionFailed(connectionResult));
        }
    }
}
