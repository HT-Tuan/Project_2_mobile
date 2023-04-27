package com.project.musicapplication.util;

import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.project.musicapplication.firebase.firebaseObject;
import com.project.musicapplication.model.PlayList;
import com.project.musicapplication.model.Song;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseUtil {
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

    public static void getSongFromFirebase(String collectionName, String field, String value, OnSongListListener listener) {
        FirebaseFirestore db = firebaseObject.db;
        CollectionReference collectionRef = db.collection(collectionName);
        Query query = collectionRef.whereEqualTo(field, value).orderBy("name");
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

    public interface OnPlaylistListListener {
        void onSongList(List<PlayList> playLists);
    }

    public static void getPlaylistFromFirebase(String collectionName, String field, String value, String field2, String value2, OnPlaylistListListener listener) {
        FirebaseFirestore db = firebaseObject.db;
        CollectionReference collectionRef = db.collection(collectionName);
        Query query = collectionRef.whereEqualTo(field, value).whereEqualTo(field2, value2).orderBy("name");
        List<PlayList> playLists = new ArrayList<>();
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        PlayList playList =
                                new PlayList(
                                        document.getId(),
                                        document.getString("name"),
                                        document.getString("email"),
                                        (List<String>) document.get("listSong"),
                                        document.getString("tag"));
                        playLists.add(playList);
                    }
                    listener.onSongList(playLists);
                } else {
                    Log.d("Get document from " + collectionName + " failed", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    public interface OnSongListener {
        void onSong(Song song);
    }

    public static void getASongFromFirebaseById(String collectionName, String documentId, OnSongListener listener) {
        FirebaseFirestore db = firebaseObject.db;
        DocumentReference documentRef = db.collection(collectionName).document(documentId);
        Song song = new Song();
        documentRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        song.setId(document.getId());
                        song.setName(document.getString("name"));
                        song.setSinger(document.getString("singer"));
                        song.setLink(document.getString("link"));
                        song.setLinkimg(document.getString("linkimg"));
                        listener.onSong(song);
//                        Log.i("Song info in Yeu thich", documentId+":::"+song.getId() +"::"+ song.getName() +"::"+ song.getLink());
                    } else {
                        Log.d("Get document from " + collectionName + " failed", "No such document");
                    }
                } else {
                    Log.d("Get document from " + collectionName + " failed", "Error getting document: ", task.getException());
                }
            }
        });

    }
    public interface OnUploadAudioFileListener {
        void onUploadSuccess(String audioUrl);
        void onUploadFailure(String errorMessage);
    }

    public static void uploadAudioFileToFirebaseStorage(Song song, final OnUploadAudioFileListener listener) {
        final StorageReference storageRef = firebaseObject.storage.getReference();

        // Create a "musics" folder in Firebase Storage if it doesn't exist
        StorageReference musicRef = storageRef.child("musics");

        // Create a new file name based on the current time
        String filename = song.getName() + System.currentTimeMillis() + ".mp3";
        StorageReference audioRef = musicRef.child(filename);

        // Get the audio file Uri from the Song object
        String filePath = song.getLink();
        File audioFile = new File(filePath);
        Uri audioFileUri = Uri.fromFile(audioFile);

        // Upload the audio file to Firebase Storage
        UploadTask uploadTask = audioRef.putFile(audioFileUri);

        // Get the download URL for the audio file
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Get the download URL for the audio file
                return audioRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    // File upload successful, send the download URL back to the listener
                    Uri downloadUri = task.getResult();
                    listener.onUploadSuccess(downloadUri.toString());
                } else {
                    // File upload failed
                    listener.onUploadFailure("");
                }
            }
        });
    }


    public interface OnUploadSongListener {
        void onUploadSuccess(String songId);
        void onUploadFailure(String e);
    }

    public static void addSongToFirestore(Song song, OnUploadSongListener listener) {
        FirebaseFirestore db = firebaseObject.db;
        CollectionReference collectionRef = db.collection("songs");
        Map<String, Object> songMap = new HashMap<>();
        songMap.put("name", song.getName());
        songMap.put("singer", song.getSinger());
        songMap.put("link", song.getLink());
        songMap.put("linkimg", "");

        collectionRef.add(songMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d("UPLOAD", "DocumentSnapshot added with ID: " + documentReference.getId());
                listener.onUploadSuccess("Upload success");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("UPLOAD", "Error adding document", e);
                listener.onUploadFailure(e.getMessage());
            }
        });
    }

    public static void updatePlaylistInFirebase(String collectionName, String email, String tag, List<String> newSongList, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        FirebaseFirestore db = firebaseObject.db;
        CollectionReference collectionRef = db.collection(collectionName);

        Query query = collectionRef.whereEqualTo("email", email).whereEqualTo("tag", tag);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String docId = document.getId();
                        Map<String, Object> updates = new HashMap<>();
                        updates.put("listSong", newSongList);
                        collectionRef.document(docId).update(updates);
                    }
                } else {
                    Log.d("Get document from " + collectionName + " failed", "Error getting documents: ", task.getException());
                }
            }
        });
    }
}