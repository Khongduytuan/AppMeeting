package com.example.appmeeting.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appmeeting.R;
import com.example.appmeeting.utilities.Constants;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    private EditText inputEmailToReset;
    private MaterialButton buttonResetPassword;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        inputEmailToReset = findViewById(R.id.inputEmailToReset);
        buttonResetPassword = findViewById(R.id.buttonResetPassword);
        buttonResetPassword.setOnClickListener(v -> {
            if(inputEmailToReset.getText().toString().trim().isEmpty()){
                Toast.makeText(ProfileActivity.this, "Enter email", Toast.LENGTH_SHORT).show();
            } else if (!Patterns.EMAIL_ADDRESS.matcher(inputEmailToReset.getText().toString()).matches()) {
                Toast.makeText(ProfileActivity.this, "Enter valid email", Toast.LENGTH_SHORT).show();
            } else{
                String email = inputEmailToReset.getText().toString();
//                sendPasswordResetEmail(email);
                updatePassword();
//                updateEmail(email);
                Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                startActivity(intent);
            }
        });
    }

    private void updatePassword() {
        FirebaseAuth.getInstance().addAuthStateListener(auth -> {
            FirebaseUser user = auth.getCurrentUser();
            if (user != null && user.isEmailVerified()) {
                String newPassword = "12345678"; // Thay đổi thành mật khẩu mới của người dùng

                // Cập nhật mật khẩu mới trong Firestore
                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                DocumentReference userRef = firestore.collection(Constants.KEY_COLLECTION_USERS).document(user.getUid());
                userRef.update(Constants.KEY_PASSWORD, newPassword)
                        .addOnSuccessListener(aVoid -> {
                            System.out.println("Đã cập nhật mật khẩu mới trong Firestore");
                        })
                        .addOnFailureListener(e -> {
                            System.out.println("Lỗi khi cập nhật mật khẩu mới trong Firestore: " + e.getMessage());
                        });
            }
        });
    }

    private void sendPasswordResetEmail(String email) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        try {
            auth.sendPasswordResetEmail(email);
            Toast.makeText(this, "Reset successful, please check your mailbox ", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    private void updateEmail(String email){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.updateEmail("user@example.com")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ProfileActivity.this, "Update email successfully", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}