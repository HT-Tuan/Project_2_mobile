package com.project.musicapplication.util;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.project.musicapplication.firebase.firebaseObject;
import com.project.musicapplication.model.Song;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GetDataFromFirebaseUtil {
    public interface OnSongListListener {
        void onSongList(List<Song> songList);
    }

    public static void getSongFromFirebase(String collectionName, int number, OnSongListListener listener) {
        FirebaseFirestore db = firebaseObject.db;
        CollectionReference collectionRef = db.collection(collectionName);
        Query query = collectionRef.orderBy("name").limit(number);
        List<Song> mSongList = new ArrayList<>();
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Song song = new Song();
                        song.setId(document.getId());
                        song.setName(document.getString("name"));
                        song.setSinger(document.getString("singer"));
                        song.setLink(document.getString("link"));
                        song.setLinkimg(document.getString("linkimg"));
                        mSongList.add(song);
                    }
                    listener.onSongList(mSongList);
                } else {
                    Log.d("Get document from " + collectionName + " failed", "Error getting documents: ", task.getException());
                }
            }
        });
    }

}
