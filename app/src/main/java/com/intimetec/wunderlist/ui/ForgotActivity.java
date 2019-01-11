package com.intimetec.wunderlist.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.intimetec.wunderlist.R;
import com.intimetec.wunderlist.util.ConnectionUtil;
import com.intimetec.wunderlist.util.Util;

public class ForgotActivity extends BaseActivity {
    private EditText inputEmail;

    private Button btnReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);
        inputEmail = findViewById(R.id.forgot_email_edt);
        btnReset = findViewById(R.id.forgot_password_btn);

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptForgotPassword();
            }
        });
    }

    private void attemptForgotPassword() {
        String email = inputEmail.getText().toString().trim();
        inputEmail.setError(null);
        if (TextUtils.isEmpty(email)) {
            inputEmail.setError(getString(R.string.empty_field_error));
        } else if (!Util.isEmailValid(email)) {
            inputEmail.setError(getString(R.string.email_invalid_msg));
        } else {
            if (ConnectionUtil.isConnected(getApplicationContext())) {
                hideSoftKeyboard(getCurrentFocus());
                showProgressDialog();
                forgotPassword(email);
            } else {
                Toast.makeText(getApplicationContext(), "Please check your internet connection!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void forgotPassword(String email) {
        mFireBaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        hideProgressDialog();
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Check email to reset your password!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(ForgotActivity.this, LoginActivity.class));
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "Failed to send reset password email!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}




