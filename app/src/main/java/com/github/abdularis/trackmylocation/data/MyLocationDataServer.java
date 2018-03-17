package com.github.abdularis.trackmylocation.data;

import android.location.Location;
import android.util.Log;

import com.github.abdularis.trackmylocation.model.SharedLocation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

public class MyLocationDataServer {

    private static final String TAG = "MyLocationDataServer";

    private boolean mInitialized;
    private String mDevId;
    private BehaviorSubject<String> mDevIdSubject;
    private FirebaseUser user;
    private DocumentReference mDocRef;
    private SharedLocation mSharedLocation;

    public MyLocationDataServer() {
        mDevIdSubject = BehaviorSubject.create();
        init();
    }

    public Observable<String> getDevIdObservable() {
        return mDevIdSubject;
    }

    public void setCurrentLocation(Location location) {
        if (mInitialized) {

            mSharedLocation.setLocation(new SharedLocation.LatLong(location.getLatitude(), location.getLongitude()));
            if (user.getDisplayName() != null) mSharedLocation.setName(user.getDisplayName());
            if (user.getPhotoUrl() != null) mSharedLocation.setPhotoUrl(user.getPhotoUrl().toString());

            mDocRef.set(mSharedLocation);
        } else {
            init();
        }
    }

    public void clearCurrentLocation() {
        if (mInitialized) {
            mDocRef.delete()
                    .addOnSuccessListener(aVoid -> Log.v(TAG, mDevId + " deleted"))
                    .addOnFailureListener(e -> Log.v(TAG, "Delete failure: " + e.toString()));
        }
    }

    private void init() {
        mInitialized = false;

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        mSharedLocation = new SharedLocation();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("users")
                .document(user.getUid())
                .addSnapshotListener((documentSnapshot, e) -> {
                    if (e != null) {
                        Log.v(TAG, e.toString());
                    } else {
                        String devId = documentSnapshot.getString("devId");
                        if (devId != null) {
                            mDevIdSubject.onNext(devId);
                            if (!mInitialized) {
                                mInitialized = true;
                                mDevId = devId;
                            }

                            if (!devId.equals(mDevId)) {
                                clearCurrentLocation();
                                mDevId = devId;
                                mDocRef = null;
                            }

                            if (mDocRef == null) {
                                mDocRef = FirebaseFirestore.getInstance()
                                        .collection("shared_locations")
                                        .document(mDevId);
                                mSharedLocation.setDevId(mDevId);
                            }
                        }
                    }
                });
    }

}
