package com.intimetec.wunderlist;

import android.app.ProgressDialog;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;

import util.ConnectionUtil;

public class EmailVerifyActivity extends AppCompatActivity {
    private static final String TAG = RegisterActivity.class.getCanonicalName();
    private EditText verifyEmailEdittext, verifyPasswordEdittext;
    private Button emailVeifyButton;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verify);
        firebaseAuth = FirebaseAuth.getInstance();
        verifyEmailEdittext = (EditText) findViewById(R.id.email_verify_edt);
        verifyPasswordEdittext = (EditText) findViewById(R.id.password_verify_edt);
        emailVeifyButton = (Button) findViewById(R.id.email_verification_btn);
        progressDialog = new ProgressDialog(this);
        emailVeifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptEmailVerify();
            }
        });

    }

    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.hide();
        }
    }


    private void attemptEmailVerify() {
        String email = verifyEmailEdittext.getText().toString().trim();
        String password = verifyPasswordEdittext.getText().toString().trim();

        verifyEmailEdittext.setError(null);
        verifyPasswordEdittext.setError(null);
        if (TextUtils.isEmpty(email)) {
            verifyEmailEdittext.setError(getString(R.string.empty_field_error));
        } else if (!isEmailValid(email)) {
            verifyEmailEdittext.setError(getString(R.string.email_invalid_msg));
        } else if (TextUtils.isEmpty(password)) {
            verifyPasswordEdittext.setError(getString(R.string.empty_field_error));
        } else if (!isPasswordValid(password)) {
            verifyPasswordEdittext.setError(getString(R.string.password_invalid_msg));
        } else {
            if (ConnectionUtil.isConnected(getApplicationContext())) {
                showProgressDialog();
                emailVerify(email, password);
            } else {
                Toast.makeText(getApplicationContext(), "Please check your internet connection!", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void emailVerify(String email, String password) {
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        // Re-enable button
                        findViewById(R.id.email_verification_btn).setEnabled(true);
                        hideProgressDialog();
                        if (task.isSuccessful()) {
                            Toast.makeText(EmailVerifyActivity.this,
                                    "Verification email sent to " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(EmailVerifyActivity.this, LoginActivity.class));
                            finish();
                        } else {
                            Log.e(TAG, "sendEmailVerification", task.getException());
                            Toast.makeText(EmailVerifyActivity.this,
                                    "Failed to send verification email." + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(getString(R.string.progress_message));
            progressDialog.setIndeterminate(true);
        }

        progressDialog.show();
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 6;
    }

    private boolean isEmailValid(String email) {
        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(email).matches();
    }

}
