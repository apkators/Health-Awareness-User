package com.health.threat.awareness.user.app;

import android.content.Context;
import android.content.SharedPreferences;

public class AppPreferenceManager {
    private static final String PREF_NAME = "UserApp_Prefs";
    private static final String IS_USER_LOGGED_IN = "is_logged_in";
    private static final String USER_TYPE = "user_type";
    private static final String DEVICE_TOKEN = "device_token";

    private final SharedPreferences sharedPreferences;
    private final int PRIVATE_MOOD = 0;
    private SharedPreferences.Editor prefsEditor;

    public AppPreferenceManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MOOD);
    }

    public void Clear() {
        sharedPreferences.edit().clear().apply();
    }

    public String getUserType() {
        return sharedPreferences.getString(USER_TYPE, "");
    }

    public void setUserType(String userType) {
        prefsEditor = sharedPreferences.edit();
        prefsEditor.putString(USER_TYPE, userType);
        prefsEditor.apply();
    }

    public boolean isUserLoggedIn() {
        return sharedPreferences.getBoolean(IS_USER_LOGGED_IN, false);
    }

    public void setUserLoggedIn(boolean isUserLoggedIn) {
        prefsEditor = sharedPreferences.edit();
        prefsEditor.putBoolean(IS_USER_LOGGED_IN, isUserLoggedIn);
        prefsEditor.apply();
    }

    public String getDeviceToken() {
        return sharedPreferences.getString(DEVICE_TOKEN, "");
    }

    public void setDeviceToken(String deviceToken) {
        prefsEditor = sharedPreferences.edit();
        prefsEditor.putString(DEVICE_TOKEN, deviceToken);
        prefsEditor.apply();
    }

}