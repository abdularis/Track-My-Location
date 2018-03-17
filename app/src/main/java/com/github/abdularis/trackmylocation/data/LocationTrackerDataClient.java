package com.github.abdularis.trackmylocation.data;

import android.util.Log;

import com.github.abdularis.trackmylocation.model.SharedLocation;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

public class LocationTrackerDataClient {

    private BehaviorSubject<SharedLocation> mTrackedLocationSubject;
    private ListenerRegistration registration;

    public LocationTrackerDataClient() {
        mTrackedLocationSubject = BehaviorSubject.create();
    }

    public void startTracking(String devId) {
         registration = FirebaseFirestore.getInstance()
                .collection("shared_locations")
                .document(devId)
                .addSnapshotListener((documentSnapshot, e) -> {
                    if (documentSnapshot.exists()) {
                        SharedLocation sharedLocation = documentSnapshot.toObject(SharedLocation.class);
                        mTrackedLocationSubject.onNext(sharedLocation);
                        Log.v("Tracker", "doc exists");
                    }

                    Log.v("Tracker", "doc not exists");
                });
    }

    public void stopTracking() {
        if (registration != null) {
            registration.remove();
        }
    }

    public Observable<SharedLocation> getLocationUpdate() {
        return mTrackedLocationSubject;
    }
}
