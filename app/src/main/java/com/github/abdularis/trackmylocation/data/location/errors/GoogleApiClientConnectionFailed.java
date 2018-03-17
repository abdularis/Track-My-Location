package com.github.abdularis.trackmylocation.data.location.errors;

import com.google.android.gms.common.ConnectionResult;

public class GoogleApiClientConnectionFailed extends RuntimeException {

    private final ConnectionResult mConnectionResult;

    public GoogleApiClientConnectionFailed(ConnectionResult connectionResult) {
        mConnectionResult = connectionResult;
    }

    @Override
    public String getMessage() {
        return "Connection to google api client failed: please check an internet connection";
    }

    public ConnectionResult getConnectionResult() {
        return mConnectionResult;
    }
}
