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

    public GoogleApiClientFlowableOnSubscribe(Context context) {
        mContext = context.getApplicationContext();
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
                Log.i(TAG, "Google api client reconnecting...");
            }


        } else {
            Log.i(TAG, "Creating new google api client instance");

            googleApiClient = new GoogleApiClient.Builder(mContext)
                    .addConnectionCallbacks(apiClientCallback)
                    .addOnConnectionFailedListener(apiClientCallback)
                    .addApi(LocationServices.API)
                    .build();
            googleApiClient.connect();
        }

        e.setCancellable(() -> {
            if (googleApiClient != null && googleApiClient.isConnected()) {
                onUnsubscribe(googleApiClient);
                googleApiClient.unregisterConnectionCallbacks(apiClientCallback);
                googleApiClient.unregisterConnectionFailedListener(apiClientCallback);
                googleApiClient.disconnect();
                apiClientCallback.emitter = null;
            }
        });
    }

    protected abstract void onGoogleApiClientReady(GoogleApiClient apiClient, FlowableEmitter<T> e);

    protected abstract void onUnsubscribe(GoogleApiClient apiClient);

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
