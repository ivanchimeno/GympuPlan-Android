package com.ivanchimeno.gympuplan;

import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import com.ivanchimeno.gympuplan.lib.GympuWrapper;


public class SettingsActivity extends ActionBarActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Display the fragment as the main content.
        SettingsFragment settingsFragment = new SettingsFragment();
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
    }

    public static class SettingsFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.prefs_settings);

            // Populate preferences.
            populatePreferences();
        }

        private void populatePreferences()
        {
            GympuWrapper.UserAccount userAccount = GympuWrapper.Instance().UserAccount();

            Preference pref = findPreference("username");
            pref.setSummary(userAccount.username);

            pref = findPreference("display_name");
            pref.setSummary(userAccount.displayName);

            pref = findPreference("grade");
            pref.setSummary(userAccount.userGrade);

            pref = findPreference("group");
            pref.setSummary(userAccount.userGroup);
        }
    }
}
