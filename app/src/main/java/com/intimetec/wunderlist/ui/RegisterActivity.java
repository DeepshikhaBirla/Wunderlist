package com.intimetec.wunderlist.ui;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.intimetec.wunderlist.R;
import com.intimetec.wunderlist.data.user.User;
import com.intimetec.wunderlist.util.ConnectionUtil;
import com.intimetec.wunderlist.util.Util;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends BaseActivity {
    private static final String TAG = RegisterActivity.class.getCanonicalName();
    private EditText registerEmailEditTxt, registerPasswordEditTxt, userNameEditTxt;
    private Button loginButton, registerButton;

    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        registerEmailEditTxt = findViewById(R.id.email_edit_txt1);
        registerPasswordEditTxt = findViewById(R.id.password_edit_txt1);
        userNameEditTxt = findViewById(R.id.name_edit_text);
        loginButton = findViewById(R.id.register_btn);
        registerButton = findViewById(R.id.signup_btn);

        db = getFireStoreInstance();

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    private void signUp() {
        String name = userNameEditTxt.getText().toString().trim();
        String email = registerEmailEditTxt.getText().toString().trim();
        String password = registerPasswordEditTxt.getText().toString().trim();


        userNameEditTxt.setError(null);
        registerEmailEditTxt.setError(null);
        registerPasswordEditTxt.setError(null);

        if (TextUtils.isEmpty(name)) {
            userNameEditTxt.setError(getString(R.string.empty_field_error));
        } else if (TextUtils.isEmpty(email)) {
            registerEmailEditTxt.setError(getString(R.string.empty_field_error));
        } else if (!Util.isEmailValid(email)) {
            registerEmailEditTxt.setError(getString(R.string.email_invalid_msg));
        } else if (TextUtils.isEmpty(password)) {
            registerPasswordEditTxt.setError(getString(R.string.empty_field_error));
        } else if (!Util.isPasswordValid(password)) {
            registerPasswordEditTxt.setError(getString(R.string.password_invalid_msg));
        } else {
            if (ConnectionUtil.isConnected(getApplicationContext())) {
                hideSoftKeyboard(getCurrentFocus());
                showProgressDialog();
                createUser(email, password);

            } else {
                Toast.makeText(getApplicationContext(), "Please check your internet connection!", Toast.LENGTH_SHORT).show();
            }
            Toast.makeText(this, "Saved Data", Toast.LENGTH_LONG).show();
        }
    }

    private void createUser(final String email, String password) {
        mFireBaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        hideProgressDialog();
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mFireBaseAuth.getCurrentUser();
                            user.sendEmailVerification();

                            Toast.makeText(getApplicationContext(), "Email verification link is sent. " +
                                    "please verify your mail address", Toast.LENGTH_LONG).show();

                            User loginUser = new User();
                            loginUser.setUserName(userNameEditTxt.getText().toString());
                            loginUser.setUserEmail(user.getEmail());
                            loginUser.setUserId(user.getUid());

                            Map<String, User> userMap = new HashMap<>();
                            userMap.put("user", loginUser);

                            db.collection("users").document(email).set(userMap, SetOptions.merge())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.w("RegisterActivity", "DocumentSnapshot Successfully written");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("RegisterActivity", "Error writing document", e);
                                        }
                                    });

                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed." + task.getException().getLocalizedMessage(),
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }
}