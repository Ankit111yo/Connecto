                                                  CONNECTO

A social media app where a user can post text,images.
User can like,comment in posts and can share through external apps.
User can follow different users.
Has an inbuilt messaging service.

LIBRARIES USED:

Firebase
Glide


PROJECT SETUP:

After cloning the project, you need to replace the existing Firebase json file with yours. Follow the steps below to add json file to your project:
1:Create Firebase account and go to console ( it is present in upper right side of main website).

2:Click on New Project. A dialog box opens up. Give a suitable name and click on Create Project.

3:In the new window, where you have to select the platform in which you want to add Firebase, select Android.

4:Register app window opens. Fill the details.

5:Package Name : com.example.android.connecto
  App Nickname : Connecto
  
6:Click on Register App button.

7:Download the json file and put it in the app directory.

8:Build the project in Android Studio. And click OK in Firebase and the project is created.

9:Next when the below window opens, go to Sign-in Method in Authentication tab.

10:Enable Email and Password.

11:Setup Realtime Database from Database tab. Make sure you have the following rules in your rules section of database.
{ "rules": { ".read": "auth != null", ".write": "auth != null" } }.

12:Similarly, setup Firebase Storage from Storage tab. Check the rules again.
service firebase.storage { match /b/{bucket}/o { match /{allPaths=**} { allow read, write: if request.auth != null; } } }








