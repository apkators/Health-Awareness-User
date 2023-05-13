package com.health.threat.awareness.user.services;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.health.threat.awareness.user.LoginActivity;
import com.health.threat.awareness.user.app.AppPreferenceManager;
import com.health.threat.awareness.user.notification.FirebaseNotification;

import java.util.Objects;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "mFirebaseMsgService";
    String title, message;
    DatabaseReference databaseReference;

    public MyFirebaseMessagingService() {
        super();
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        title = remoteMessage.getData().get("Title");
        message = remoteMessage.getData().get("Message");

        FirebaseNotification.notify(getApplicationContext(), title, message, "default", this);

    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }

    @Override
    public void onMessageSent(@NonNull String s) {
        super.onMessageSent(s);
        Log.d(TAG, "onMessageSent: ");
    }

    @Override
    public void onSendError(@NonNull String s, @NonNull Exception e) {
        super.onSendError(s, e);
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);

        new AppPreferenceManager(getApplicationContext()).setDeviceToken(s);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            firebaseAuth.getCurrentUser().getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference().child("UsersDeviceTokens").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            databaseReference.child("DeviceToken").setValue(s);
        }
    }

    @Override
    protected Intent getStartCommandIntent(Intent intent) {
        return super.getStartCommandIntent(intent);
    }

    @Override
    public boolean handleIntentOnMainThread(Intent intent) {
        return super.handleIntentOnMainThread(intent);
    }

    @Override
    public void handleIntent(Intent intent) {
        super.handleIntent(intent);
    }
}
