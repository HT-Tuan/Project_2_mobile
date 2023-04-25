package com.project.musicapplication.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project.musicapplication.MainActivity;
import com.project.musicapplication.R;
import com.project.musicapplication.firebase.firebaseObject;
import com.project.musicapplication.model.MyUser;
import com.project.musicapplication.util.ActivityUtil;

public class SignUpActivity extends AppCompatActivity {
    TextInputLayout emailEditTextLayout, passwordEditTextLayout;
    ImageView backToMain;
    Button letSignup;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_page);
        emailEditTextLayout = findViewById(R.id.signup_email);
        passwordEditTextLayout = findViewById(R.id.signup_password);
        backToMain = findViewById(R.id.backToMain);
        letSignup = findViewById(R.id.letSignup);

        backToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK);
                finish();
            }
        });

        letSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditTextLayout.getEditText().getText().toString().trim();
                String password = passwordEditTextLayout.getEditText().getText().toString().trim();
                if(email == null || password == null)
                {
                    Toast.makeText(SignUpActivity.this, "Please fill all field.", Toast.LENGTH_SHORT).show();
                }
                else {
                    signup(email, password);
                }
            }
        });
    }

    private void signup(String email, String password) {
        firebaseObject.mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            firebaseObject.user = firebaseObject.mAuth.getCurrentUser();

                            // Create a new user object with email and add to Firestore
                            MyUser newUser = new MyUser(email, password);
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            db.collection("users").document(firebaseObject.user.getUid())
                                    .set(newUser)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Sign up and data added to Firestore successful
                                            // Navigate to the next activity or do something else
                                            Log.i("Signup Success", "Adding document Success: "+email);
                                            setResult(RESULT_OK);
                                            finish();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Failed to add user data to Firestore
                                            Log.w("Signup Failure", "Error adding document", e);
                                        }
                                    });

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SignUpActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
