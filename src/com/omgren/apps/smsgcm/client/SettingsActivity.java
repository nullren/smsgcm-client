package com.omgren.apps.smsgcm.client;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity {
    public static final String PREFS_NAME = "SettingsFile";
    public static final String PREF_CERT_PASSWORD = "pref_cert_password";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.settings);
    }
}
