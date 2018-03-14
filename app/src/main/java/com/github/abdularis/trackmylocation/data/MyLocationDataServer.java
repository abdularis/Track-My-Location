package com.github.abdularis.trackmylocation.data;

import android.location.Location;

import com.github.abdularis.trackmylocation.model.TrackedLocation;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;
import java.util.Observable;

public class MyLocationDataServer {

    private boolean mInitialized;
    private String mDevId;
    private FirebaseUser user;
    private DocumentReference mDocRef;
    private TrackedLocation trackedLocation;

    public MyLocationDataServer() {
        mInitialized = false;

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        trackedLocation = new TrackedLocation();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("users")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    mDevId = documentSnapshot.getString("dev_id");
                    if (mDevId != null) {
                        mInitialized = true;
                        trackedLocation.setDevId(mDevId);
                    }
                });
    }

    public void setCurrentLocation(Location location) {
        if (mInitialized) {
            if (mDocRef == null) {
                mDocRef = FirebaseFirestore.getInstance()
                        .collection("tracked_locations")
                        .document(mDevId);
            }

            trackedLocation.setLocation(new TrackedLocation.LatLong(location.getLatitude(), location.getLongitude()));
            trackedLocation.setName(user.getDisplayName());
            trackedLocation.setPhotoUrl(Objects.requireNonNull(user.getPhotoUrl()).toString());

            mDocRef.set(trackedLocation);
        }
    }

}
