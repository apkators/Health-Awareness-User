package com.health.threat.awareness.user;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        Intent intent;
        if (user != null) {
            intent = new Intent(SplashActivity.this, LocationActivity.class);
        } else {
            intent = new Intent(SplashActivity.this, LoginActivity.class);
        }
        startActivity(intent);
        SplashActivity.this.finish();
    }
}