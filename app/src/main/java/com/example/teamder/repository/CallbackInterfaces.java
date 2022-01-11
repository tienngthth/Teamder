package com.example.teamder.repository;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class CallbackInterfaces {

    public interface EmptyCallBack {
        void onCallBack();
    }

    public interface VoidCallBack {
        void onCallBack(Void v);
    }

    public interface DocRefCallBack {
        void onCallBack(DocumentReference documentReference);
    }

    public interface QuerySnapShotCallBack {
        void onCallBack(QuerySnapshot querySnapshot);
    }

    public interface DocumentSnapshotCallBack {
        void onCallBack(DocumentSnapshot documentSnapshot);
    }

    public interface DocumentSnapshotsCallBack {
        void onCallBack(List<DocumentSnapshot> documentSnapshots);
    }

    public interface FirebaseUserCallBack {
        void onCallBack(FirebaseUser firebaseUser);
    }

    public interface ListDocumentChangeCallBack {
        void onCallBack(List<DocumentChange> documentChangeList);
    }
}
