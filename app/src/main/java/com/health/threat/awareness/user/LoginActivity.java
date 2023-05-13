package com.health.threat.awareness.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.health.threat.awareness.user.app.AppPreferenceManager;
import com.health.threat.awareness.user.model.AppUser;
import com.health.threat.awareness.user.util.InternetDialog;

import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class LoginActivity extends AppCompatActivity {
    EditText mEmailEditText, mPasswordEditText;
    TextView LoginTextView;
    TextView forgotPasswordTextView;
    TextView signUpActivity;
    private FirebaseAuth mAuth;
    private SweetAlertDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        //Initialization
        mEmailEditText = findViewById(R.id.username_input);
        mPasswordEditText = findViewById(R.id.pass);
        forgotPasswordTextView = findViewById(R.id.forgotPassword);
        LoginTextView = findViewById(R.id.loginButton);
        signUpActivity = findViewById(R.id.signUpActivity);

        signUpActivity.setOnClickListener(v -> StartSignUpActivity());
        forgotPasswordTextView.setOnClickListener(v -> StartForgotPasswordActivity());

        request_notification_api13_permission();

        // Login with Email and Password
        LoginTextView.setOnClickListener(v -> {
            if (new InternetDialog(this).getInternetStatus()) {
                if (ValidateEmailAndPassword()) {

                    pDialog = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                    pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                    pDialog.setTitleText("Login in process ...");
                    pDialog.setCancelable(false);
                    pDialog.show();

                    String email = Objects.requireNonNull(mEmailEditText.getText()).toString().trim();
                    String password = Objects.requireNonNull(mPasswordEditText.getText()).toString().trim();

                    //authenticate user
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LoginActivity.this, task -> {
                                if (task.isSuccessful()) {
                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                    databaseReference.child("AppUsers").child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            AppUser user = dataSnapshot.getValue(AppUser.class);
                                            if (user != null) {
                                                pDialog.dismiss();

                                                String token = new AppPreferenceManager(LoginActivity.this).getDeviceToken();
                                                DatabaseReference deviceTokenRef = FirebaseDatabase.getInstance().getReference().child("UsersDeviceTokens").child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
                                                deviceTokenRef.child("DeviceToken").setValue(token);

                                                Intent intent = new Intent(LoginActivity.this, LocationActivity.class);
                                                overridePendingTransition(R.anim.animation_enter_back, R.anim.animation_back_leave);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                mAuth.signOut();
                                                pDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                                                pDialog.setTitle("Oops...");
                                                pDialog.setContentText("User not registered!");
                                                pDialog.setCancelable(true);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            mAuth.signOut();
                                            // there was an error
                                            pDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                                            pDialog.setTitle("Oops...");
                                            pDialog.setContentText("User not registered!");
                                            pDialog.setCancelable(true);
                                            // Failed to read value
                                            //Toast.makeText(getContext(), "Failed to read value ",Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else {
                                    // there was an error
                                    pDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                                    pDialog.setTitle("Oops...");
                                    pDialog.setContentText("User not registered!");
                                    pDialog.setCancelable(true);
                                }
                            });
                }
            }
        });
    }

    private void request_notification_api13_permission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (this.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 22);
            }
        }
    }

    private boolean ValidateEmailAndPassword() {
        String emailAddress = Objects.requireNonNull(mEmailEditText.getText()).toString().trim();
        if (mEmailEditText.getText().toString().equals("")) {
            mEmailEditText.setError("please enter email address");
            mEmailEditText.requestFocus();
            return false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()) {
            mEmailEditText.setError("please enter valid email address");
            mEmailEditText.requestFocus();
            return false;
        }
        if (Objects.requireNonNull(mPasswordEditText.getText()).toString().equals("")) {
            mPasswordEditText.setError("please enter password");
            mPasswordEditText.requestFocus();
            return false;
        }
        if (Objects.requireNonNull(mPasswordEditText.getText()).toString().length() < 8) {
            mPasswordEditText.setError("password minimum contain 8 character");
            mPasswordEditText.requestFocus();
            return false;
        }
        return true;
    }

    private void StartForgotPasswordActivity() {
        Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.animation_enter_back, R.anim.animation_back_leave);
        finish();
    }

    private void StartSignUpActivity() {
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.animation_enter_back, R.anim.animation_back_leave);
        finish();
    }
}