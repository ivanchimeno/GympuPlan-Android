package com.ivanchimeno.gympuplan;

import java.util.Locale;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.ivanchimeno.gympuplan.lib.GympuWrapper;


public class VPlanViewerActivity extends ActionBarActivity implements ActionBar.TabListener
{
    // Hold the type of schedule currently being displayed.
    // The default is student because they have less power
    // than teachers..
    private ScheduleType mCurrentDisplayedSchedule = ScheduleType.STUDENT;

    // The schedules may contain multiple images so
    // hold them inside a list.
    // Since a WebView control is used, only
    // image urls will be retrieved.
    private String[] mScheduleTodayUrls, mScheduleTomorrowUrls;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    // Display a ProgressDialog when sending
    // Http requests.
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vplan_viewer);

        // Called this method otherwise SSL Handshake failure occurs.
        SSLCertificateHandler.nuke();

        if (savedInstanceState == null)
        {
            // Retrieve stored username and password.
            initialize();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_vplan_viewer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        switch (id)
        {
            case R.id.action_settings:
                Utilities.StartSettingsIntent(this);
                break;

            case R.id.action_refresh:
                refresh();
                break;

            case R.id.action_switch_schedule:
                // This function is only available for teachers so
                // do a user group check before calling it.
                if (GympuWrapper.Instance().UserAccount().userGroup.equals(GympuWrapper.UserGroup.Teacher)
                        || GympuWrapper.Instance().UserAccount().userGroup.equals(GympuWrapper.UserGroup.Lehrer))
                    onSwitchScheduleRequested();
                else
                    Toast.makeText(this, R.string.function_not_available, Toast.LENGTH_SHORT).show();

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop()
    {
        // Retrieve the preference where the user states
        // if he would like his account information to be saved
        // onto the device.
        boolean shouldSave = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("checkBox", true);

        // Remove all account information if the user doesn't want to.
        if (!shouldSave)
        {
            // Remove saved user account information from Wrapper class.
            GympuWrapper.Instance().UserAccount().clean();

            // Remove saved user account information from device.
            Utilities.ClearUserSettings(this);
        }

        super.onStop();
    }

    @Override
    /**
     * Using this method to capture when the user has successfully
     * authenticated.
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == Utilities.ActivityRequestCodes.LOGIN_ACTIVITY_REQUEST)
        {
            // Activity result came from the LoginActivity meaning the user
            // has authenticated successfully.
            if (resultCode == 0)
            {
                // Save user account information.
                saveUserPreferences();

                // Set the current schedule to display corresponding to
                // the user group.
                String userGroup = GympuWrapper.Instance().UserAccount().userGroup;

                if (userGroup.equals(GympuWrapper.UserGroup.Lehrer)
                        || userGroup.equals(GympuWrapper.UserGroup.Teacher))
                    mCurrentDisplayedSchedule = ScheduleType.TEACHER;
                else
                    mCurrentDisplayedSchedule = ScheduleType.STUDENT;

                // Show the progress dialog.
                mProgressDialog = ProgressDialog.show(this, getResources().getString(R.string.please_wait), getResources().getString(R.string.authenticating), true);

                // Initialize schedules.
                initializeSchedule();
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction)
    {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction)
    {
        // Nothing to implement here.
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction)
    {
        // Nothing to implement here.
    }

    private void refresh()
    {
        initialize();
    }

    /**
     * The user would like to change the type of schedule to display.
     */
    private void onSwitchScheduleRequested()
    {
        // Switch the type of schedule that is currently being displayed.
        // If the current schedule type is student, then change to teacher.
        // If the current schedule type is teacher, then change to student.
        mCurrentDisplayedSchedule = (mCurrentDisplayedSchedule.equals(ScheduleType.TEACHER)) ? ScheduleType.STUDENT :
                ScheduleType.TEACHER;

        // Initialize the schedules.
        initializeSchedule();
    }

    /**
     * Initializes the schedules.
     */
    private void initializeSchedule()
    {
        // Start the schedule downloader task.
        ScheduleDownloadTask task = new ScheduleDownloadTask();
        task.execute((Void) null);
    }

    /**
     * Initializes the ActionBar, PagerAdapter, and ViewPager.
     */
    private void initializeComponents()
    {
        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mSectionsPagerAdapter.notifyDataSetChanged();

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        if(actionBar.getTabCount() == 0)
        {
            // For each of the sections in the app, add a tab to the action bar.
            for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
                // Create a tab with text corresponding to the page title defined by
                // the adapter. Also specify this Activity object, which implements
                // the TabListener interface, as the callback (listener) for when
                // this tab is selected.
                actionBar.addTab(
                        actionBar.newTab()
                                .setText(mSectionsPagerAdapter.getPageTitle(i))
                                .setTabListener(this));
            }
        }
    }

    /**
     * Displays the LoginActivity if user account information
     * is not stored in the device.
     */
    private void initialize()
    {
        // Retrieve stored username and password.
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final String username = sharedPreferences.getString("username", null);
        final String password = sharedPreferences.getString("password", null);

        // Display the Login Activity if user data is not already stored
        // in the device.
        if (username == null || password == null)
        {
            Utilities.StartLoginIntent(this);
        }
        else
        {
            mProgressDialog = ProgressDialog.show(this, getResources().getString(R.string.please_wait), getResources().getString(R.string.authenticating), true);

            new Thread(new Runnable() {
                @Override
                public void run() {

                    // Authenticate the user.
                    boolean isAuthenticated = GympuWrapper.Instance().Login(username, password);

                    // Start the LoginActivity if the user couldn't be
                    // authenticated.
                    if (!isAuthenticated)
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                Utilities.StartLoginIntent(VPlanViewerActivity.this);
                            }
                        });
                    else
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                // Set the current schedule to display corresponding to
                                // the user group.
                                String userGroup = GympuWrapper.Instance().UserAccount().userGroup;

                                if (userGroup.equals(GympuWrapper.UserGroup.Lehrer)
                                        || userGroup.equals(GympuWrapper.UserGroup.Teacher))
                                    mCurrentDisplayedSchedule = ScheduleType.TEACHER;
                                else
                                    mCurrentDisplayedSchedule = ScheduleType.STUDENT;

                                // Initialize Schedules.
                                initializeSchedule();
                            }
                        });
                }
            }).start();
        }
    }

    /**
     * Saves the user account to the SharedPreferences so that the
     * user doesn't always have to enter it.
     */
    private void saveUserPreferences()
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.edit().putString("username", GympuWrapper.Instance().UserAccount().username).apply();
        sharedPreferences.edit().putString("password", GympuWrapper.Instance().UserAccount().password).apply();
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter
    {
        public SectionsPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {
            // Create the fragment.
            ScheduleViewerFragment fragment = new ScheduleViewerFragment();

            switch (position)
            {
                // Return Fragment displaying today's schedule.
                case 0:
                    fragment.setBitmapUrls(mScheduleTodayUrls);
                    return fragment;

                // Return Fragment displaying tomorrows schedule.
                case 1:
                    fragment.setBitmapUrls(mScheduleTomorrowUrls);
                    return fragment;
                default:
                    return null;
            }
        }

        @Override
        public int getCount()
        {
            // The pager adapter hold 2 Views.
            // One for the today's schedule, and one for tomorrows.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            Locale l = Locale.getDefault();
            switch (position)
            {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
            }
            return null;
        }
    }

    /**
     * Represents an asynchronous schedule download task used to download the
     * schedule images.
     */
    public class ScheduleDownloadTask extends AsyncTask<Void, Void, Boolean>
    {
        ScheduleDownloadTask()
        {
            // Nothing to do here.
        }

        @Override
        protected Boolean doInBackground(Void... params)
        {
            // Download the appropriate schedules.
            if (mCurrentDisplayedSchedule.equals(ScheduleType.STUDENT))
            {
                mScheduleTodayUrls = GympuWrapper.Instance().DownloadPlanUrls(GympuWrapper.PlanID.StudentToday);
                mScheduleTomorrowUrls= GympuWrapper.Instance().DownloadPlanUrls(GympuWrapper.PlanID.StudentTomorrow);
            }

            else if (mCurrentDisplayedSchedule.equals(ScheduleType.TEACHER))
            {
                mScheduleTodayUrls = GympuWrapper.Instance().DownloadPlanUrls(GympuWrapper.PlanID.TeacherToday);
                mScheduleTomorrowUrls= GympuWrapper.Instance().DownloadPlanUrls(GympuWrapper.PlanID.TeacherTomorrow);
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success)
        {
            // Dismiss the progress dialog if it is showing.
            if (mProgressDialog.isShowing())
                mProgressDialog.dismiss();

            // All required data has been downloaded,
            // initialize the rest of the components.
            initializeComponents();
        }
    }
}
