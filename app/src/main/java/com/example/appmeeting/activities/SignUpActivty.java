package com.example.appmeeting.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appmeeting.R;
import com.example.appmeeting.utilities.Constants;
import com.example.appmeeting.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.regex.Pattern;

public class SignUpActivty extends AppCompatActivity {
    private EditText inputFirstName, inputLastName, inputEmail, inputPassWord, inputConfirmPassWord;
    private MaterialButton buttonSignUp;
    private ProgressBar signUpProgressBar;

    private PreferenceManager preferenceManager;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_activty);
        preferenceManager = new PreferenceManager(getApplicationContext());

        findViewById(R.id.imageBack).setOnClickListener(v -> onBackPressed());
        findViewById(R.id.textSignIn).setOnClickListener(v -> onBackPressed());

        inputFirstName = findViewById(R.id.inputFirstName);
        inputLastName = findViewById(R.id.inputLastName);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassWord = findViewById(R.id.inputPassWord);
        inputConfirmPassWord = findViewById(R.id.inputConfirmPassWord);
        signUpProgressBar = findViewById(R.id.signUpProgressBar);
        buttonSignUp = findViewById(R.id.buttonSignUp);
        buttonSignUp.setOnClickListener(v -> {
            if (inputFirstName.getText().toString().trim().isEmpty()){
                Toast.makeText(SignUpActivty.this, "Enter first name", Toast.LENGTH_SHORT).show();
            } else if (inputLastName.getText().toString().trim().isEmpty()) {
                Toast.makeText(SignUpActivty.this, "Enter last name", Toast.LENGTH_SHORT).show();
            } else if (inputEmail.getText().toString().trim().isEmpty()) {
                Toast.makeText(SignUpActivty.this, "Enter email", Toast.LENGTH_SHORT).show();
            } else if (!Patterns.EMAIL_ADDRESS.matcher(inputEmail.getText().toString()).matches()) {
                Toast.makeText(SignUpActivty.this, "Enter valid email", Toast.LENGTH_SHORT).show();
            } else if (inputPassWord.getText().toString().trim().isEmpty()) {
                Toast.makeText(SignUpActivty.this, "Enter password", Toast.LENGTH_SHORT).show();
            } else if (inputConfirmPassWord.getText().toString().trim().isEmpty()) {
                Toast.makeText(SignUpActivty.this, "Enter confirm password", Toast.LENGTH_SHORT).show();
            } else if (!inputPassWord.getText().toString().equals(inputConfirmPassWord.getText().toString())) {
                Toast.makeText(SignUpActivty.this, "Password and confirm password must be same", Toast.LENGTH_SHORT).show();
            }
            else {
                signUp();
            }
        });
    }
    public void signUp(){
        buttonSignUp.setVisibility(View.INVISIBLE);
        signUpProgressBar.setVisibility(View.VISIBLE);
//        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
//        String email = inputEmail.getText().toString().trim();
//        String password = inputPassWord.getText().toString().trim();
//        firebaseAuth.createUserWithEmailAndPassword(email, password)
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
                        // Người dùng đăng ký thành công
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        HashMap<String, Object> user = new HashMap<>();
        user.put(Constants.KEY_FIRST_NAME, inputFirstName.getText().toString());
        user.put(Constants.KEY_LAST_NAME, inputLastName.getText().toString());
        user.put(Constants.KEY_EMAIL, inputEmail.getText().toString());
        user.put(Constants.KEY_PASSWORD, inputPassWord.getText().toString());

        database.collection(Constants.KEY_COLLECTION_USERS)
                .add(user)
                .addOnSuccessListener(documentReference -> {
                    preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                    preferenceManager.putString(Constants.KEY_USER_ID, documentReference.getId());
                    preferenceManager.putString(Constants.KEY_FIRST_NAME, inputFirstName.getText().toString());
                    preferenceManager.putString(Constants.KEY_LAST_NAME, inputLastName.getText().toString());
                    preferenceManager.putString(Constants.KEY_EMAIL, inputEmail.getText().toString());

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    buttonSignUp.setVisibility(View.VISIBLE);
                    signUpProgressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(SignUpActivty.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
//                    } else {
//                        Toast.makeText(this, "Error Sign Up", Toast.LENGTH_SHORT).show();
//                    }
//                });

    }
}