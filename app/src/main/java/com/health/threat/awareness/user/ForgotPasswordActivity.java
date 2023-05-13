package com.health.threat.awareness.user;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.health.threat.awareness.user.util.InternetDialog;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ForgotPasswordActivity extends AppCompatActivity {
    EditText emailTdtTxt;
    TextView sendEmailButton;
    FirebaseAuth auth;
    TextView alreadyRegistered_TextView;
    SweetAlertDialog pDialog;
    private InternetDialog internetDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        internetDialog = new InternetDialog(ForgotPasswordActivity.this);

        emailTdtTxt = findViewById(R.id.emailEdtTxt);
        sendEmailButton = findViewById(R.id.sendEmailBtn);
        alreadyRegistered_TextView = findViewById(R.id.back_to_Login_textView);

        alreadyRegistered_TextView.setOnClickListener(v -> {
            startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
            overridePendingTransition(R.anim.animation_enter, R.anim.animation_leave);
            ForgotPasswordActivity.this.finish();
        });

        auth = FirebaseAuth.getInstance();

        sendEmailButton.setOnClickListener(v -> {
            if (!internetDialog.getInternetStatus()) {
                return;
            }

            String email = emailTdtTxt.getText().toString().trim();
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(getApplication(), "Enter your mail address", Toast.LENGTH_SHORT).show();
                return;
            }

            pDialog = new SweetAlertDialog(ForgotPasswordActivity.this, SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setTitleText("Sending Reset Email ...");
            pDialog.setCancelable(false);
            pDialog.show();

            auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            pDialog.changeAlertType(SweetAlertDialog.WARNING_TYPE);
                            pDialog.setTitleText("Reset Email");
                            pDialog.setContentText("We have sent reset email to you. Please Check Email!");
                            pDialog.setConfirmText("OK");
                            pDialog.setConfirmClickListener(SweetAlertDialog::dismissWithAnimation);
                            pDialog.show();
                        } else {
                            pDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                            pDialog.setTitleText("Oops...")
                                    .setConfirmText("OK")
                                    .setConfirmClickListener(SweetAlertDialog::dismissWithAnimation)
                                    .setContentText("Your email not exist Or " + task.getException())
                                    .show();
                        }
                    });
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
        overridePendingTransition(R.anim.animation_enter, R.anim.animation_leave);
        this.finish();
    }
}