package com.github.abdularis.trackmylocation.data.rxfirestore;

import com.github.abdularis.trackmylocation.data.rxfirestore.errors.DocumentNotExistsException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.Disposable;

public class FirestoreDocumentOnSubscribe implements ObservableOnSubscribe<DocumentSnapshot> {

    DocumentReference mDocumentReference;

    FirestoreDocumentOnSubscribe(DocumentReference documentReference) {
        mDocumentReference = documentReference;
    }

    @Override
    public void subscribe(ObservableEmitter<DocumentSnapshot> e) throws Exception {
        DocEventListener listener = new DocEventListener(e);
        ListenerRegistration registration = mDocumentReference.addSnapshotListener(listener);
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

    class DocEventListener implements EventListener<DocumentSnapshot> {

        ObservableEmitter<DocumentSnapshot> emitter;

        DocEventListener(ObservableEmitter<DocumentSnapshot> emitter) {
            this.emitter = emitter;
        }

        @Override
        public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
            if (e != null) {
                emitter.onError(e);
            } else {
                if (documentSnapshot.exists()) {
                    emitter.onNext(documentSnapshot);
                } else {
                    emitter.onError(new DocumentNotExistsException());
                }
            }
        }
    }
}
