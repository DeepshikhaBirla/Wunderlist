package com.intimetec.wunderlist.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.intimetec.wunderlist.R;
import com.intimetec.wunderlist.data.user.User;
import com.intimetec.wunderlist.data.user.UserRepository;
import com.intimetec.wunderlist.util.ConnectionUtil;
import com.intimetec.wunderlist.util.PreferenceManager;
import com.intimetec.wunderlist.util.Util;

import java.util.HashMap;

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = LoginActivity.class.getCanonicalName();
    private EditText loginEmail, loginPassword;
    private Button loginButton, registerButton;

    private TextView forgotTextview, emailVerify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailVerify = findViewById(R.id.email_verification_txt_view);
        forgotTextview = findViewById(R.id.forgot_password_txt_view);
        loginEmail = findViewById(R.id.email_edit_txt);
        loginPassword = findViewById(R.id.password_edit_txt);
        loginButton = findViewById(R.id.login_btn);
        registerButton = findViewById(R.id.register_btn);

        loginButton.setOnClickListener(this);
        emailVerify.setOnClickListener(this);
        registerButton.setOnClickListener(this);
        forgotTextview.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == loginButton) {
            attemptSignIn();
        } else if (view == emailVerify) {
            startActivity(new Intent(LoginActivity.this, EmailVerifyActivity.class));
        } else if (view == registerButton) {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        } else if (view == forgotTextview) {
            startActivity(new Intent(LoginActivity.this, ForgotActivity.class));
        }
    }

    private void attemptSignIn() {
        String email = loginEmail.getText().toString().trim();
        String password = loginPassword.getText().toString().trim();

        loginEmail.setError(null);
        loginPassword.setError(null);
        if (TextUtils.isEmpty(email)) {
            loginEmail.setError(getString(R.string.empty_field_error));
        } else if (!Util.isEmailValid(email)) {
            loginEmail.setError(getString(R.string.email_invalid_msg));
        } else if (TextUtils.isEmpty(password)) {
            loginPassword.setError(getString(R.string.empty_field_error));
        } else if (!Util.isPasswordValid(password)) {
            loginPassword.setError(getString(R.string.password_invalid_msg));
        } else {
            if (ConnectionUtil.isConnected(getApplicationContext())) {
                hideSoftKeyboard(getCurrentFocus());
                showProgressDialog();
                signIn(email, password);
            } else {
                Toast.makeText(getApplicationContext(), "Please check your internet connection!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void signIn(final String email, String password) {
        mFireBaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mFireBaseAuth.getCurrentUser();

                            if (user.isEmailVerified()) {

                                final FirebaseFirestore db = getFireStoreInstance();

                                db.collection("users").document(user.getEmail()).get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                final UserRepository userRepository = new UserRepository(getApplication());

                                                HashMap<String, String> userMap = (HashMap<String, String>) task.getResult().get("user");
                                                final User loginUser = new User();
                                                loginUser.setUserId(userMap.get("userId"));
                                                loginUser.setUserEmail(userMap.get("userEmail"));
                                                loginUser.setUserName(userMap.get("userName"));
                                                loginUser.setDeviceId(Util.getDeviceId(getApplicationContext()));

                                                HashMap<String, User> map = new HashMap<>();
                                                map.put("user", loginUser);

                                                db.collection("users").document(email).set(map, SetOptions.merge())
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                hideProgressDialog();
                                                                userRepository.add(loginUser);
                                                                PreferenceManager.setUserLogin(LoginActivity.this, true);
                                                                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                                                finish();
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                hideProgressDialog();
                                                            }
                                                        });

                                            }
                                        });
                            } else {
                                hideProgressDialog();
                                Toast.makeText(getApplicationContext(), "Please verify your mail address", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            // If sign in fails, display a message to the user.
                            hideProgressDialog();
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed." + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}

