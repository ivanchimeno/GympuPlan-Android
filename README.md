# GympuPlan Android
An unofficial simple Android subtitute viewer application for Otfried-Preußler-Gymnasium Pullach.

GympuPlan is an unofficial and simple Android application for viewing teacher substitute plans at the Otfried-Preußler-Gymnasium Pullach in Munich, Germany. A wrapper class is provided to help young students implement their own substitute plan viewer application.

##### Application Download
The application is available for free on  the Google Play Store [here](https://play.google.com/store/apps/details?id=com.gympuplan&hl=en).

##### Importing the Project
The project was created using the [Android Studio](http://developer.android.com/sdk/index.html) so can directly be imported by selecting "Import Project" in the Quick Start menu.

##### Developing your own Application
You can use the `GympuWrapper` wrapper class to access the internal student and teacher network of the school and implement your own substitute viewing application.

You just need to import the following classes into your Android application project:
  *	GympuWrapper.java
  *	GympuXmlParser.java
  *	GympuResponse.java

##### GympuWrapper Usage
The GympuWrapper class provides a few simple methods to access the network and retrieve data associated with the substitute plans.

GympuWrapper methods can be accessed by calling `GympuWrapper.Instance()`.

Available GympuWrapper methods:
  * `boolean Login(String username, String password);` Authenticates the user and initializes the session.
  * `void Logout();` Terminates the session and removes user account information.
  * `int VPlanPageNumber(int planId);` Returns the number of pages from a given plan.
  * `int VPlanLastUpdate(int planId);` Returns the timestamp of the last update plan update.
  * `Bitmap[] DownloadPlan(int planId);` Downloads all images of a given plan.
  * `String[] DownloadPlanUrls(int planId);` Returns urls representing the images of a given plan.
  * `String GenerateHTML(String[] planUrls);` Generates HTML code of the given plan image urls.
  * `UserAccount UsertAccount();` Returns information about the connected user.

There are four types of substitude plans that you can retrieve. These plan identifiers can be accessed by calling `GympuWrapper.PlanID`.

Available PlanIds:
  * `StudentToday`
  * `StudentTomorrow`
  * `TeacherToday`
  * `TeacherTomorrow`
  
##### Example
Here a small code snippet to demonstrate the class.
```
// Connect
boolean isAuthenticated = GympuWrapper.Instance().Login("test", "test");

if(isAuthenticated)
{
      // Get the number of pages for the student plan current day.
      int pageNumber = GympuWrapper.Instance().VPlanPageNumber(GympuWrapper.PlanID.StudentToday);
      
      // Download the plan for the student next day.
      Bitmap[] planImagesTomorrow = GympuWrapper.Instance().DownloadPlan(GympuWrapper.PlanID.StudentToday);
      
      // If you're using a WebView you can download image urls and 
      // generate the HTML code for displaying.
      String[] planImageUrlsTomorrow = GympuWrapper.Instance().DownloadPlanUrls(GympuWrapper.PlanID.StudentToday);
      
      // Generate HTML.
      String htmlCode = GympuWrapper.Instance().GenerateHTML(planImageUrlsTomorrow);
      
      // Load inside a WebView.
       webView.loadDataWithBaseURL("", html, "text/html", "UTF-8", "");
       
      // Retrieve some information about the user.
      GympuWrapper.UserAccount userAccount = GympuWrapper.Instance().UserAccount();
      
      String displayName = userAccount.displayName;
      String userGrade = userAccount.userGrade;
      String userGroup = userAccount.userGroup;
}
```

##### LICENSE
This software is provided under the MIT license: [http://opensource.org/licenses/mit-license.php](http://opensource.org/licenses/mit-license.php)

##### Author
[Ivan Chimeno](https://github.com/ivanchimeno)






