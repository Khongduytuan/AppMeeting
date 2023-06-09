package com.example.appmeeting.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appmeeting.R;
import com.example.appmeeting.utilities.Constants;
import com.example.appmeeting.utilities.PreferenceManager;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class SignInActivity extends AppCompatActivity {
    private EditText inputEmail, inputPassWord;
    private MaterialButton buttonSignIn;
    private ProgressBar signInProgressBar;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_actvity);

        preferenceManager = new PreferenceManager(getApplicationContext());
        if (preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN)){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
        findViewById(R.id.textSignUp).setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), SignUpActivty.class)));
        inputEmail = findViewById(R.id.inputEmail);
        inputPassWord = findViewById(R.id.inputPassWord);
        signInProgressBar = findViewById(R.id.signInProgressBar);
        buttonSignIn = findViewById(R.id.buttonSignIn);

        buttonSignIn.setOnClickListener(v -> {
            if(inputEmail.getText().toString().trim().isEmpty()){
                Toast.makeText(SignInActivity.this, "Enter email", Toast.LENGTH_SHORT).show();
            } else if (inputPassWord.getText().toString().trim().isEmpty()) {
                Toast.makeText(SignInActivity.this, "Enter password", Toast.LENGTH_SHORT).show();
            }else if (!Patterns.EMAIL_ADDRESS.matcher(inputEmail.getText().toString()).matches()) {
                Toast.makeText(SignInActivity.this, "Enter valid email", Toast.LENGTH_SHORT).show();
            } else{
                signIn();
            }
        });
    }

    private void signIn() {
        buttonSignIn.setVisibility(View.INVISIBLE);
        signInProgressBar.setVisibility(View.VISIBLE);

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_EMAIL, inputEmail.getText().toString())
                .whereEqualTo(Constants.KEY_PASSWORD, inputPassWord.getText().toString())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0){
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        Log.d("signIn", "documentSnapshot: " + documentSnapshot );
                        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                        preferenceManager.putString(Constants.KEY_USER_ID, documentSnapshot.getId());
                        preferenceManager.putString(Constants.KEY_FIRST_NAME, documentSnapshot.getString(Constants.KEY_FIRST_NAME));
                        preferenceManager.putString(Constants.KEY_LAST_NAME, documentSnapshot.getString(Constants.KEY_LAST_NAME));
                        preferenceManager.putString(Constants.KEY_EMAIL, documentSnapshot.getString(Constants.KEY_EMAIL));

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                    else {
                        buttonSignIn.setVisibility(View.INVISIBLE);
                        signInProgressBar.setVisibility(View.VISIBLE);
                        Toast.makeText(SignInActivity.this, "Unable sign in", Toast.LENGTH_SHORT).show();
                    }

                });
    }
}