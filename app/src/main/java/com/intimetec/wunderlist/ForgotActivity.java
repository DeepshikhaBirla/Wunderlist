package com.intimetec.wunderlist;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

import util.ConnectionUtil;

public class ForgotActivity extends AppCompatActivity {
    public class ForgotPasswordActivity extends AppCompatActivity {
        private EditText inputEmail;

        private Button btnReset;
        private ProgressDialog progressDialog;
        private FirebaseAuth firebaseauth;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_forgot);
            inputEmail = (EditText) findViewById(R.id.forgot_email_edt);
            btnReset = (Button) findViewById(R.id.forgot_password_btn);
            btnReset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    attemptForgotPassword();
                }
            });

        }

        private void hideProgressDialog() {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.hide();
            }
        }

        private void attemptForgotPassword() {
            String email = inputEmail.getText().toString().trim();
            inputEmail.setError(null);
            if (TextUtils.isEmpty(email)) {
                inputEmail.setError(getString(R.string.empty_field_error));
            } else if (!isEmailValid(email)) {
                inputEmail.setError(getString(R.string.email_invalid_msg));
            } else {
                if (ConnectionUtil.isConnected(getApplicationContext())) {
                    showProgressDialog();
                    forgotPassword(email);
                } else {
                    Toast.makeText(getApplicationContext(), "Please check your internet connection!", Toast.LENGTH_SHORT).show();
                }
            }
        }

        private void forgotPassword(String email) {

            firebaseauth.sendPasswordResetEmail(email);

            firebaseauth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            hideProgressDialog();
                            if (task.isSuccessful()) {
                                Toast.makeText(ForgotPasswordActivity.this, "Check email to reset your password!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ForgotPasswordActivity.this, "Fail to send reset password email!", Toast.LENGTH_SHORT).show();
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

        private boolean isEmailValid(String email) {
            return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                    + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                    + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                    + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(email).matches();
        }
    }
}




