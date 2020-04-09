package com.example.android.scanwire.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.example.android.scanwire.Constants;
import com.example.android.scanwire.R;
import com.google.firebase.BuildConfig;//TODO

public class SettingsFragment extends PreferenceFragmentCompat implements
        Preference.OnPreferenceClickListener {

    private SharedPreferences mSharedPreferences;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        // Set Title
        getActivity().setTitle(getString(R.string.action_settings));

        // Dark Theme
        Preference darkTheme = findPreference(getString(R.string.dark_theme_key));
        darkTheme.setOnPreferenceClickListener(this);

        // About (Other Apps)
        Preference aboutOtherApps = findPreference(getString(R.string.about_other_apps_key));
        aboutOtherApps.setOnPreferenceClickListener(this);

        // About (App)
        Preference aboutApp = findPreference(getString(R.string.about_app_key));
        aboutApp.setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if(preference == findPreference(getString(R.string.dark_theme_key))) { // Dark Theme
            boolean nightMode = mSharedPreferences.getBoolean(getString(R.string.dark_theme_key),
                    getResources().getBoolean(R.bool.default_night_mode));
            if (nightMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        } else if (preference == findPreference(getString(R.string.about_other_apps_key))) { // View other Apps
            Uri uri = Uri.parse(getContext().getString(R.string.dev_github));
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            getContext().startActivity(intent);
        } else if (preference == findPreference(getString(R.string.about_app_key))) { // About App
            // Inflate View
            View dialogView = View.inflate(getActivity(), R.layout.dialog_about, null);

            // Set HTML link for 'libraries'
            TextView libraries = dialogView.findViewById(R.id.dialog_about_libraries_textview);
            libraries.setMovementMethod(LinkMovementMethod.getInstance());
            libraries.setText(Html.fromHtml(getString(R.string.about_libraries)));

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(getString(R.string.app_name));
            builder.setView(dialogView);
            builder.setPositiveButton(R.string.ok, null);
            builder.show();
        }
        return true;
    }
}
