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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.net.Socket;
import java.util.regex.Pattern;

import util.ConnectionUtil;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getCanonicalName();
    private EditText loginEmail, loginPassword;
    private Button loginButton, registerButton;
    private ProgressDialog progressDialog;
    private TextView forgotTextview, emailVerify;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        emailVerify = (TextView) findViewById(R.id.email_verification_txt_view);
        forgotTextview = (TextView) findViewById(R.id.forgot_password_txt_view);
        loginEmail = (EditText) findViewById(R.id.email_edit_txt);
        loginPassword = (EditText) findViewById(R.id.password_edit_txt);
        loginButton = (Button) findViewById(R.id.login_btn);
        registerButton = (Button) findViewById(R.id.register_btn);
        progressDialog = new ProgressDialog(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSignIn();
            }
        });

        emailVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, EmailVerifyActivity.class));
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));

            }
        });

        forgotTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgotActivity.class));
            }
        });
    }


    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.hide();
        }
    }

    private void attemptSignIn() {
        String email = loginEmail.getText().toString().trim();
        String password = loginPassword.getText().toString().trim();

        loginEmail.setError(null);
        loginPassword.setError(null);
        if (TextUtils.isEmpty(email)) {
            loginEmail.setError(getString(R.string.empty_field_error));
        } else if (!isEmailValid(email)) {
            loginEmail.setError(getString(R.string.email_invalid_msg));
        } else if (TextUtils.isEmpty(password)) {
            loginPassword.setError(getString(R.string.empty_field_error));
        } else if (!isPasswordValid(password)) {
            loginPassword.setError(getString(R.string.password_invalid_msg));
        } else {
            if (ConnectionUtil.isConnected(getApplicationContext())) {
                showProgressDialog();
                signIn(email, password);
            } else {
                Toast.makeText(getApplicationContext(), "Please check your internet connection!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void signIn(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();

                            if (user.isEmailVerified()) {
                                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                            } else {
                                Toast.makeText(getApplicationContext(), "Please verify your mail address", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed." + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(LoginActivity.this);
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

