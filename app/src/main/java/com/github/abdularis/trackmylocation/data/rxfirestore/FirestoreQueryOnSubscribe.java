package com.github.abdularis.trackmylocation.data.rxfirestore;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.disposables.Disposable;

public class FirestoreQueryOnSubscribe implements FlowableOnSubscribe<QuerySnapshot> {

    private Query mQuery;

    public FirestoreQueryOnSubscribe(Query query) {
        mQuery = query;
    }

    @Override
    public void subscribe(FlowableEmitter<QuerySnapshot> e) throws Exception {
        QueryEventListener listener = new QueryEventListener(e);
        ListenerRegistration registration = mQuery.addSnapshotListener(listener);
        e.setDisposable(new Disposable() {
            boolean disposed = false;
            @Override
            public void dispose() {
                if (!isDisposed()) {
                    registration.remove();
                    listener.emitter = null;
                    disposed = true;
                }
            }

            @Override
            public boolean isDisposed() {
                return disposed;
            }
        });
    }

    class QueryEventListener implements EventListener<QuerySnapshot> {

        FlowableEmitter<QuerySnapshot> emitter;

        QueryEventListener(FlowableEmitter<QuerySnapshot> emitter) {
            this.emitter = emitter;
        }

        @Override
        public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
            if (e != null) {
                emitter.onError(e);
            } else {
                emitter.onNext(documentSnapshots);
            }
        }
    }
}
