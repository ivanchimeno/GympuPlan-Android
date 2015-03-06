package com.ivanchimeno.gympuplan.lib;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

/**
 * GympuWrapper is a singleton class that kind of implements the GymPuLan API
 * written by Christian Spitschka.
 */
public class GympuWrapper
{
    /**
     * GympuWrapper is a singleton class, to access
     * its' methods just call GympuWrapper.Instance().
     */
    private static GympuWrapper instance = null;

    /**
     * User Account information coming from the server
     * are stored here.
     * Information can be cleared by calling the Logout() method.
     */
    private UserAccount mUserAccount;

    /**
     * Prevents manual instantiation.
     */
    protected GympuWrapper()
    {
        mUserAccount = new UserAccount();
    }

    /**
     * Sends a login request to the server. If authentication
     * is successful, you can retrieve all account information
     * by calling GympuWrapper.Instance().UserAccount()
     * @param username
     * @param password
     * @return true/false if authentication succeeded
     */
    public boolean Login(String username, String password)
    {
        // Attempt sending the Http request.
        try
        {
            // Construct the login Url string.
            String loginUrlString = "https://secure.gymnasium-pullach.de/?page=api&action=Login&GymPuLanUserName=" + URLEncoder.encode(username, "UTF-8") + "&GymPuLanPassword=" + URLEncoder.encode(password, "UTF-8");

            // Retrieve the response.
            InputStream responseStream = SendHttpsRequest(loginUrlString);

            // Convert to readable GympuResponse object.
            GympuResponse response = new GympuXmlParser().Parse(responseStream);

            // Create and populate UserAccount if login was successful.
            if (Integer.parseInt(response.StatusCode()) == StatusCodes.LoginOk)
            {
                mUserAccount.username = username;
                mUserAccount.password = password;
                mUserAccount.displayName = response.GympuLanDisplayName();
                mUserAccount.userGrade = response.GympuLanUserGrade();
                mUserAccount.userGroup = response.GympuLanUserGroup();
                mUserAccount.sessionId = response.GympuLanSessionId();

                return true;
            }
            else
                return false;
        }
        catch (Exception e)
        {
           // Exception occurred, return false.
            return  false;
        }
    }

    /**
     * Sends a logout request to the server, terminating the
     * current session.
     * Cleans UserAccount object by assigning null to each
     * variable.
     */
    public void Logout()
    {
        // Construct the login Url string.
        String logoutUrlString = "https://secure.gymnasium-pullach.de/?page=api&action=Logout&gplsession=" + mUserAccount.sessionId;

        // Send the Http request.
        try
        {
            // Retrieve the response.
            InputStream responseStream = SendHttpsRequest(logoutUrlString);

            // Convert to readable GympuResponse object.
            GympuResponse response = new GympuXmlParser().Parse(responseStream);

            // Retrieve the response code.
            int responseCode = Integer.parseInt(response.StatusCode());

            // TODO: Finish implementing this method.
            if (responseCode == StatusCodes.LogoutSuccess)
            {
            }
        }
        catch (Exception ex)
        {
            // Meh!
        }
        finally
        {
            // Clean user account information.
            mUserAccount.clean();
        }
    }

    /**
     * Returns the number of pages for the given schedule.
     * You need to make sure that the session id is valid otherwise,
     * if an error has happened, this function will just return 0.
     * @param planId : The id of the schedule, found in GympuWrapper.PlanID
     * @return number of pages or 0 if error.
     */
    public int VPlanPageNumber(int planId)
    {
        // Construct the url.
        String requestUrlString = "https://secure.gymnasium-pullach.de/?page=api&action=GetVPlanStatus&gplsession=" + mUserAccount.sessionId + "&GPLVPlanID=" + planId;

        // Send the Http request.
        try
        {
            // Retrieve the response.
            InputStream responseStream = SendHttpsRequest(requestUrlString);

            // Convert to readable GympuResponse object.
            GympuResponse response = new GympuXmlParser().Parse(responseStream);

            // Retrieve the response code.
            int responseCode = Integer.parseInt(response.StatusCode());

            // Return the number of pages if the action was successful.
            if (responseCode == StatusCodes.ActionSuccess)
                return Integer.parseInt(response.GympuLanVPlanNumberOfPages());

            // Received other status code other than success so
            // return 0.
            return 0;
        }
        catch (Exception ex)
        {
            return 0;
        }
    }

    /**
     * Downloads the schedule images for the given Id.
     * Returns a list of Bitmaps or null if an error occurred.
     * @param planId : The id of the schedule, found in GympuWrapper.PlanID
     * @return
     */
    public Bitmap[] DownloadPlan(int planId)
    {
        // Construct the url.
        String requestUrlString = "https://secure.gymnasium-pullach.de/?page=api&action=GetVPlanStatus&gplsession=" + mUserAccount.sessionId + "&GPLVPlanID=" + planId;

        // Send the Http request.
        try
        {
            // Retrieve the response.
            InputStream responseStream = SendHttpsRequest(requestUrlString);

            // Convert to readable GympuResponse object.
            GympuResponse response = new GympuXmlParser().Parse(responseStream);

            // Retrieve the response code.
            int responseCode = Integer.parseInt(response.StatusCode());

            // Check if we can access the schedule data.
            // If not then just return.
            if (responseCode != StatusCodes.ActionSuccess)
                return null;

            // Initialize the list that will hold the Bitmaps.
            Bitmap[] bitmaps = new Bitmap[Integer.parseInt(response.GympuLanVPlanNumberOfPages())];

            // Construct the schedule download request url based on the planId.
            String scheduleUrlString = null;
            switch (planId)
            {
                case PlanID.StudentToday:
                    scheduleUrlString = "https://secure.gymnasium-pullach.de/?page=vplan&gplsession=" + mUserAccount.sessionId + "&action=download&id=" + URLEncoder.encode(PlanPrefix.StudentToday, "UTF-8");
                    break;

                case PlanID.StudentTomorrow:
                    scheduleUrlString = "https://secure.gymnasium-pullach.de/?page=vplan&gplsession=" + mUserAccount.sessionId + "&action=download&id=" + URLEncoder.encode(PlanPrefix.StudentTomorrow, "UTF-8");
                    break;

                case PlanID.TeacherToday:
                    scheduleUrlString = "https://secure.gymnasium-pullach.de/?page=vplan&gplsession=" + mUserAccount.sessionId + "&action=download&id=" + URLEncoder.encode(PlanPrefix.TeacherToday, "UTF-8");
                    break;

                case PlanID.TeacherTomorrow:
                    scheduleUrlString = "https://secure.gymnasium-pullach.de/?page=vplan&gplsession=" + mUserAccount.sessionId + "&action=download&id=" + URLEncoder.encode(PlanPrefix.TeacherTomorrow, "UTF-8");
                    break;

                default:
                    break;
            }

            // Return if Url string still null.
            if (scheduleUrlString == null)
                return null;

            // Retrieve the number of pages.
            int schedulePageCount = Integer.parseInt(response.GympuLanVPlanNumberOfPages());

            // Download the schedule.
            for (int i = 0; i < schedulePageCount; i++)
            {
                String scheduleUrl = scheduleUrlString + (i+1);

                // Download Bitmap.
                Bitmap bitmap = BitmapFactory.decodeStream(SendHttpsRequest(scheduleUrl));

                // Add to the list.
                bitmaps[i] = bitmap;
            }

            return bitmaps;
        }
        catch (Exception ex)
        {
            return null;
        }
    }

    /**
     * Instead of downloading the schedule images directly, you
     * can use this method to retrieve a list of the image URLs.
     * These URLs can be later on used to display the schedule images
     * inside a WebView.
     * Returns null if an error has occurred.
     * @param planId
     * @return
     */
    public String[] DownloadPlanUrls(int planId)
    {
        // Construct the url.
        String requestUrlString = "https://secure.gymnasium-pullach.de/?page=api&action=GetVPlanStatus&gplsession=" + mUserAccount.sessionId + "&GPLVPlanID=" + planId;

        // Send the Http request.
        try
        {
            // Retrieve the response.
            InputStream responseStream = SendHttpsRequest(requestUrlString);

            // Convert to readable GympuResponse object.
            GympuResponse response = new GympuXmlParser().Parse(responseStream);

            // Retrieve the response code.
            int responseCode = Integer.parseInt(response.StatusCode());

            // Check if we can access the schedule data.
            // If not then just return.
            if (responseCode != StatusCodes.ActionSuccess)
                return null;

            // Initialize the list that will hold the Bitmaps.
            String[] urls = new String[Integer.parseInt(response.GympuLanVPlanNumberOfPages())];

            // Construct the schedule download request url based on the planId.
            String scheduleUrlString = null;
            switch (planId)
            {
                case PlanID.StudentToday:
                    scheduleUrlString = "https://secure.gymnasium-pullach.de/?page=vplan&gplsession=" + mUserAccount.sessionId + "&action=download&id=" + URLEncoder.encode(PlanPrefix.StudentToday, "UTF-8");
                    break;

                case PlanID.StudentTomorrow:
                    scheduleUrlString = "https://secure.gymnasium-pullach.de/?page=vplan&gplsession=" + mUserAccount.sessionId + "&action=download&id=" + URLEncoder.encode(PlanPrefix.StudentTomorrow, "UTF-8");
                    break;

                case PlanID.TeacherToday:
                    scheduleUrlString = "https://secure.gymnasium-pullach.de/?page=vplan&gplsession=" + mUserAccount.sessionId + "&action=download&id=" + URLEncoder.encode(PlanPrefix.TeacherToday, "UTF-8");
                    break;

                case PlanID.TeacherTomorrow:
                    scheduleUrlString = "https://secure.gymnasium-pullach.de/?page=vplan&gplsession=" + mUserAccount.sessionId + "&action=download&id=" + URLEncoder.encode(PlanPrefix.TeacherTomorrow, "UTF-8");
                    break;

                default:
                    break;
            }

            // Return if Url string still null.
            if (scheduleUrlString == null)
                return null;

            // Retrieve the number of pages.
            int schedulePageCount = Integer.parseInt(response.GympuLanVPlanNumberOfPages());

            // Download the schedule.
            for (int i = 0; i < schedulePageCount; i++)
            {
                String scheduleUrl = scheduleUrlString + (i+1);

                // Add to the list.
                urls[i] = scheduleUrl;
            }

            return urls;
        }
        catch (Exception ex)
        {
            return null;
        }
    }

    /**
     * You can check when a schedule was updated by calling
     * this method. It returns the timestamp of the schedule
     * you'd like to know.
     * If an error occurs then the method will return null.
     * @param planId
     * @return The timestamp or null if error.
     */
    public String VPlanLastUpdate(int planId)
    {
        // Construct the url.
        String requestUrlString = "https://secure.gymnasium-pullach.de/?page=api&action=GetVPlanStatus&gplsession=" + mUserAccount.sessionId + "&GPLVPlanID=" + planId;

        // Send the Http request.
        try
        {
            // Retrieve the response.
            InputStream responseStream = SendHttpsRequest(requestUrlString);

            // Convert to readable GympuResponse object.
            GympuResponse response = new GympuXmlParser().Parse(responseStream);

            // Retrieve the response code.
            int responseCode = Integer.parseInt(response.StatusCode());

            // Check if we can access the schedule data.
            // If not then just return.
            if (responseCode != StatusCodes.ActionSuccess)
                return null;

            // Retrieve the last update timestamp.
            return response.GympuLanVPlanLastUpdate();
        }
        catch (Exception ex)
        {
           return null;
        }
    }

    /**
     * This function will generate HTML code that will include
     * all image URLS passed as a string array.
     * You can use this function if you prefer downloading the
     * schedules using the Android WebView widget. (Cannot blame you
     * the WebView has a beautiful pinch-zooming functionality which
     * I still wonder why they haven't included that yet in the normal
     * ImageView).
     * If the bitmap urls list is null or empty, the function will
     * return an empty HTML page displaying a brief error message.
     * @param bitmapUrls
     * @return
     */
    public String GenerateHTML(String[] bitmapUrls)
    {
        String htmlPrefix = "<!DOCTYPE html><html><body>";
        String htmlSuffix = "</body></html>";

        if (bitmapUrls == null || bitmapUrls.length <= 0)
            return htmlPrefix + "<p>There has been an error retrieving images.</p>" + htmlSuffix;

        String imgHtml = "";
        for (String bitmapUrl : bitmapUrls)
        {
            imgHtml = imgHtml + "<img src=\"" + bitmapUrl + "\"" + "width=100%>";
        }

        htmlPrefix = htmlPrefix + imgHtml + "</body></html>";

        return htmlPrefix;
    }

    /**
     * Returns the current instance, instantiates one if
     * instance doesn't exist.
     * @return GympuWrapper instance
     */
    public static GympuWrapper Instance()
    {
        if(instance == null)
        {
            instance = new GympuWrapper();
        }

        return instance;
    }

    /**
     * Returns user account information.
     * @return
     */
    public UserAccount UserAccount() {
        return mUserAccount;
    }

    /**
     * Sends an Http request and returns server response as a InputStream.
     * @param requestUrlString
     * @return
     * @throws Exception
     */
    private InputStream SendHttpsRequest(String requestUrlString) throws Exception
    {
        try
        {
            // Construct the URL for logging in.
            URL url = new URL(requestUrlString);

            // Open the connection.
            HttpURLConnection connection = (HttpsURLConnection)url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");


            // Retrieve response from server.
            InputStream inputStream = new BufferedInputStream(connection.getInputStream());

            // Convert the InputStream into String.
            return inputStream;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Class holding user account information.
     */
    public class UserAccount
    {
        /**
         * The actual username.
         */
        public String username;

        /**
         * The password of the user.
         */
        public String password;

        /**
         * The full name of the user.
         */
        public String displayName;

        /**
         * The grade of the user.
         */
        public String userGrade;

        /**
         * The group of the user (Teacher/Pupil).
         */
        public String userGroup;

        /**
         * Session id used for accessing data.
         */
        public String sessionId;

        /**
         * Removes all account information
         */
        public void clean()
        {
            username = null;
            password = null;
            displayName = null;
            userGrade = null;
            userGroup = null;
            sessionId = null;
        }
    }

    /**
     * All available user groups.
     */
    public static class UserGroup
    {
        public static final String Pupil = "Pupil";

        public static final String Student = "Student";

        public static final String Teacher = "Teacher";

        public static final String Lehrer = "Lehrer";
    }

    /**
     * Available schedule ids.
     */
    public static class PlanID
    {
        /**
         * Schedule for the current day, available to students and teachers.
         */
        public static final int StudentToday = 0;

        /**
         * Schedule for the next day, available to students and teachers.
         */
        public static final int StudentTomorrow = 1;

        /**
         * Schedule for the current day, available only to teachers.
         */
        public static final int TeacherToday = 2;

        /**
         * Schedule for the next day, available only to teachers.
         */
        public static final int TeacherTomorrow = 3;
    }

    /**
     * List of status codes returned by the server.
     */
    private static class StatusCodes
    {
        /**
         * Login was successful.
         */
        public static final int LoginOk = 110;

        /**
         * User account or password are incorrect.
         */
        public static final int AccountInvalid = 120;

        /**
         * Logout was successful.
         */
        public static final int LogoutSuccess = 210;

        /**
         * Action was successful.
         */
        public static final int ActionSuccess = 310;

        /**
         * The id of the schedule is invalid,
         */
        public static final int ScheduleIdInvalid = 320;

        /**
         * Access denied for the current schedule.
         */
        public static final int ScheduleAccessDenied = 330;

        /**
         * The session id has expired.
         */
        public static final int SessionExpired = 1100;

        /**
         * The session id is invalid.
         */
        public static final int SessionInvalid = 1200;

        /**
         * The action send to the server is unknown.
         */
        public static final int UnknownAction = 9999;
    }

    /**
     * Used for downloading the schedule images from the server.
     */
    private static class PlanPrefix
    {
        public static final String StudentToday = "Schueler+Heute_";

        public static final String StudentTomorrow = "Schueler+Morgen_";

        public static final String TeacherToday = "Lehrer+Heute_";

        public static final String TeacherTomorrow = "Lehrer+Morgen_";
    }
}
