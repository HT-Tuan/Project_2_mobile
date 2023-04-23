package com.project.musicapplication.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.project.musicapplication.model.MyUser;

public class firebaseObject {
    public static MyUser myUser;
    public static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    public static FirebaseUser user = mAuth.getCurrentUser();
}
