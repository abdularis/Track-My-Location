package com.github.abdularis.trackmylocation.data.rxfirestore;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;

public class RxFirestore {

    public static Flowable<QuerySnapshot> getFlowable(Query query) {
        return Flowable.create(new FirestoreQueryOnSubscribe(query), BackpressureStrategy.MISSING);
    }

    public static Observable<DocumentSnapshot> getDocument(DocumentReference documentReference) {
        return Observable.create(new FirestoreDocumentOnSubscribe(documentReference));
    }

}
