package com.ivanchimeno.gympuplan;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Contains various useful methods.
 */
public class Utilities
{
    /**
     * Starts the SettingsActivity.
     */
    public static void StartSettingsIntent(Activity activity)
    {
        Intent intent = new Intent(activity, SettingsActivity.class);
        activity.startActivityForResult(intent, ActivityRequestCodes.SETTINGS_ACTIVITY_REQUEST);
    }

    /**
     * Starts the LoginActivity.
     */
    public static void StartLoginIntent(Activity activity)
    {
        Intent intent = new Intent(activity, LoginActivity.class);
        activity.startActivityForResult(intent, ActivityRequestCodes.LOGIN_ACTIVITY_REQUEST);
    }

    /**
     * Displays a disclaimer AlertDialog which is required upon
     * startup.
     * @param activity
     */
    public static void DisplayStartupDialog(Activity activity)
    {
        new AlertDialog.Builder(activity)
                .setTitle("Achtung!")
                .setMessage("Achtung: Die einzig verbindlichen Vertretungspläne sind die, die durch Aushang in der Schule bekannt gemacht worden sind.")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    /**
     * Removes all user saved settings.
     * @param activity
     */
    public static void ClearUserSettings(Activity activity)
    {
        // Retrieve the preferences.
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);

        // Retrieve the editor.
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Clear settings and commit changes.
        editor.clear();
        editor.commit();
    }

    /**
     * Contains various Activity request codes.
     */
    public class ActivityRequestCodes
    {
        public static final int LOGIN_ACTIVITY_REQUEST = 666;
        public static final int SETTINGS_ACTIVITY_REQUEST = 667;
    }
}

