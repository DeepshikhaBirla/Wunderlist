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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;

import util.ConnectionUtil;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = RegisterActivity.class.getCanonicalName();
    private EditText registerEmail, registerPassword, username;
    private Button loginButton, registerButton;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        firebaseAuth = FirebaseAuth.getInstance();
        registerEmail = (EditText) findViewById(R.id.email_edit_txt1);
        registerPassword = (EditText) findViewById(R.id.password_edit_txt1);
        username = (EditText) findViewById(R.id.name_edit_text);
        loginButton = (Button) findViewById(R.id.register_btn);
        registerButton = (Button) findViewById(R.id.signup_btn);
        progressDialog = new ProgressDialog(this);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });
    }

    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.hide();
        }
    }

    private void signUp() {
        String name = username.getText().toString().trim();
        String email = registerEmail.getText().toString().trim();
        String password = registerPassword.getText().toString().trim();

        username.setError(null);
        registerEmail.setError(null);
        registerPassword.setError(null);
        if (TextUtils.isEmpty(name)) {
            username.setError(getString(R.string.empty_field_error));
        } else if (TextUtils.isEmpty(email)) {
            registerEmail.setError(getString(R.string.empty_field_error));
        } else if (!isEmailValid(email)) {
            registerEmail.setError(getString(R.string.email_invalid_msg));
        } else if (TextUtils.isEmpty(password)) {
            registerPassword.setError(getString(R.string.empty_field_error));
        } else if (!isPasswordValid(password)) {
            registerEmail.setError(getString(R.string.password_invalid_msg));
        } else {
            if (ConnectionUtil.isConnected(getApplicationContext())) {
                showProgressDialog();
                createUser(email, password);

            } else {
                Toast.makeText(getApplicationContext(), "Please check your internet connection!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void createUser(String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        hideProgressDialog();
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            user.sendEmailVerification();
                            Toast.makeText(getApplicationContext(), "Email verification link is sent. " +
                                    "please verify your mail address", Toast.LENGTH_LONG).show();

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

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(getString(R.string.progress_message));
            progressDialog.setIndeterminate(true);
        }

        progressDialog.show();
    }

    private boolean isPasswordValid(String email) {
        return email.length() > 6;
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

