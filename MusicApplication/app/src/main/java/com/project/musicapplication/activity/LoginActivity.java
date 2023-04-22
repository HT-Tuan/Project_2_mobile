package com.project.musicapplication.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.project.musicapplication.R;
import com.project.musicapplication.model.MyUser;

public class LoginActivity extends AppCompatActivity {
    public static MyUser myUser;
    public static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    TextInputLayout emailEditText, passwordEditText;
    Button letTheUserLogIn;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        emailEditText = findViewById(R.id.login_email);
        passwordEditText = findViewById(R.id.login_password);

        letTheUserLogIn = findViewById(R.id.letTheUserLogIn);

        letTheUserLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

    }

    private void login() {
        String email = emailEditText.getEditText().getText().toString().trim();
        String password = passwordEditText.getEditText().getText().toString().trim();
        myUser = new MyUser(email, password);
        Log.i("User login", email);
        // Truy vấn Firestore để kiểm tra xem tài khoản và mật khẩu có tồn tại hay không
        db.collection("users")
                .whereEqualTo("email", myUser.getEmail())
                .whereEqualTo("password", myUser.getPassword())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot.isEmpty()) {
                            // Nếu không có tài khoản và mật khẩu nào phù hợp trong Firestore thì thông báo lỗi
                            Toast.makeText(getApplicationContext(), "Invalid email or password", Toast.LENGTH_SHORT).show();
                        } else {
                            // Nếu có tài khoản và mật khẩu phù hợp thì xác thực người dùng
                            mAuth.signInWithEmailAndPassword(myUser.getEmail(), myUser.getPassword())
                                    .addOnCompleteListener(authTask -> {
                                        if (authTask.isSuccessful()) {
                                            // Xác thực thành công
                                            Toast.makeText(getApplicationContext(), "Login successful", Toast.LENGTH_SHORT).show();
                                        } else {
                                            // Xác thực không thành công
                                            Toast.makeText(getApplicationContext(), "Login failed: " + authTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}