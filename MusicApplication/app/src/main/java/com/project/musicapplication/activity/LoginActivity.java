package com.project.musicapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.project.musicapplication.MainActivity;
import com.project.musicapplication.R;
import com.project.musicapplication.firebase.firebaseObject;
import com.project.musicapplication.model.MyUser;
import com.project.musicapplication.util.ActivityUtil;

public class LoginActivity extends AppCompatActivity {

    TextInputLayout emailEditTextLayout, passwordEditTextLayout;
    TextInputEditText passwordEditText;
    Button letTheUserLogIn, signUpBtn;
    ImageView backToMain;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        emailEditTextLayout = findViewById(R.id.signup_email);
        passwordEditTextLayout = findViewById(R.id.signup_password);
        passwordEditText = (TextInputEditText) passwordEditTextLayout.getEditText();
        letTheUserLogIn = findViewById(R.id.letTheUserLogIn);
        signUpBtn = findViewById(R.id.changeToSignup);
        backToMain = findViewById(R.id.backToMain);

        letTheUserLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        if (passwordEditText != null) {
            passwordEditText.setTransformationMethod(new PasswordTransformationMethod());
        }
        passwordEditTextLayout.setEndIconMode(TextInputLayout.END_ICON_CUSTOM);
        passwordEditTextLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isPasswordVisible = passwordEditText.getTransformationMethod() instanceof PasswordTransformationMethod;
                if (isPasswordVisible) {
                    // Set the password to be visible
                    passwordEditText.setTransformationMethod(null);
                    passwordEditTextLayout.setEndIconDrawable(R.drawable.ic_show);
                } else {
                    // Set the password to be hidden
                    passwordEditText.setTransformationMethod(new PasswordTransformationMethod());
                    passwordEditTextLayout.setEndIconDrawable(R.drawable.ic_hide);
                }
                // Move the cursor to the end of the text
                passwordEditText.setSelection(passwordEditText.getText().length());
            }
        });

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ActivityUtil.openActivity(LoginActivity.this, SignUpActivity.class);
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        backToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    private void login() {
        String email = emailEditTextLayout.getEditText().getText().toString().trim();
        String password = passwordEditTextLayout.getEditText().getText().toString().trim();
        firebaseObject.myUser = new MyUser(email, password);
        Log.i("User login", email);
        // Truy vấn Firestore để kiểm tra xem tài khoản và mật khẩu có tồn tại hay không
        db.collection("users")
                .whereEqualTo("email", firebaseObject.myUser.getEmail())
                .whereEqualTo("password", firebaseObject.myUser.getPassword())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot.isEmpty()) {
                            // Nếu không có tài khoản và mật khẩu nào phù hợp trong Firestore thì thông báo lỗi
                            Toast.makeText(getApplicationContext(), "Invalid email or password", Toast.LENGTH_SHORT).show();
                        } else {
                            // Nếu có tài khoản và mật khẩu phù hợp thì xác thực người dùng
                            firebaseObject.mAuth.signInWithEmailAndPassword(firebaseObject.myUser.getEmail(), firebaseObject.myUser.getPassword())
                                    .addOnCompleteListener(authTask -> {
                                        if (authTask.isSuccessful()) {
                                            // Xác thực thành công
                                            Toast.makeText(getApplicationContext(), "Login successful", Toast.LENGTH_SHORT).show();
                                            setResult(RESULT_OK);
                                            firebaseObject.user = firebaseObject.mAuth.getCurrentUser();
                                            finish();
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            // Check if the signup was successful
            if (resultCode == RESULT_OK) {
                finish();
            }
        }
    }

}