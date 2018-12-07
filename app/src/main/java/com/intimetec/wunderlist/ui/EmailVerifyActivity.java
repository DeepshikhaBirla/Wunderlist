package com.intimetec.wunderlist.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.intimetec.wunderlist.R;
import com.intimetec.wunderlist.util.ConnectionUtil;
import com.intimetec.wunderlist.util.Util;

public class EmailVerifyActivity extends BaseActivity {
    private static final String TAG = RegisterActivity.class.getCanonicalName();
    private EditText mVerifyEmailEditText, mVerifyPasswordEditText;
    private Button mEmailVerifyButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verify);
        mVerifyEmailEditText = findViewById(R.id.email_verify_edt);
        mVerifyPasswordEditText = findViewById(R.id.password_verify_edt);
        mEmailVerifyButton = findViewById(R.id.email_verification_btn);


        mEmailVerifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptEmailVerify();
            }
        });

    }

    private void attemptEmailVerify() {
        String email = mVerifyEmailEditText.getText().toString().trim();
        String password = mVerifyPasswordEditText.getText().toString().trim();

        mVerifyEmailEditText.setError(null);
        mVerifyPasswordEditText.setError(null);
        if (TextUtils.isEmpty(email)) {
            mVerifyEmailEditText.setError(getString(R.string.empty_field_error));
        } else if (!Util.isEmailValid(email)) {
            mVerifyEmailEditText.setError(getString(R.string.email_invalid_msg));
        } else if (TextUtils.isEmpty(password)) {
            mVerifyPasswordEditText.setError(getString(R.string.empty_field_error));
        } else if (!Util.isPasswordValid(password)) {
            mVerifyPasswordEditText.setError(getString(R.string.password_invalid_msg));
        } else {
            if (ConnectionUtil.isConnected(getApplicationContext())) {
                hideSoftKeyboard(getCurrentFocus());
                showProgressDialog();
                emailVerify(email, password);
            } else {
                Toast.makeText(getApplicationContext(), "Please check your internet connection!", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void emailVerify(String email, String password) {
        mFireBaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(EmailVerifyActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mFireBaseAuth.getCurrentUser();

                            user.sendEmailVerification();

                            Toast.makeText(getApplicationContext(), "Email verification link is sent to : " + user.getEmail()
                                    + "Please verify your mail", Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(EmailVerifyActivity.this, LoginActivity.class));
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(EmailVerifyActivity.this, "Authentication failed." + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
