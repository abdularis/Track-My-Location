package com.github.abdularis.trackmylocation.data;

import android.util.Log;

import com.github.abdularis.trackmylocation.model.TrackedLocation;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

public class LocationTrackerDataClient {

    private BehaviorSubject<TrackedLocation> mTrackedLocationSubject;
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
                        TrackedLocation trackedLocation = documentSnapshot.toObject(TrackedLocation.class);
                        mTrackedLocationSubject.onNext(trackedLocation);
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

    public Observable<TrackedLocation> getLocationUpdate() {
        return mTrackedLocationSubject;
    }
}
