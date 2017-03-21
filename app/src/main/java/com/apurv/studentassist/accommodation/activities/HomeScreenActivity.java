package com.apurv.studentassist.accommodation.activities;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.android.volley.Request;
import com.apurv.studentassist.R;
import com.apurv.studentassist.accommodation.Dialogs.AlertDialogL;
import com.apurv.studentassist.accommodation.Dialogs.LoadingDialog;
import com.apurv.studentassist.accommodation.classes.AccommodationAdd;
import com.apurv.studentassist.accommodation.classes.University;
import com.apurv.studentassist.accommodation.classes.User;
import com.apurv.studentassist.accommodation.urlInfo.UrlGenerator;
import com.apurv.studentassist.accommodation.urlInfo.UrlInterface;
import com.apurv.studentassist.airport.activities.AirportActivity;
import com.apurv.studentassist.internet.NetworkInterface;
import com.apurv.studentassist.internet.StudentAssistBO;
import com.apurv.studentassist.notifications.receiver.RegistrationIntentService;
import com.apurv.studentassist.util.ErrorReporting;
import com.apurv.studentassist.util.L;
import com.apurv.studentassist.util.ObjectSerializer;
import com.apurv.studentassist.util.SAConstants;
import com.apurv.studentassist.util.Utilities;
import com.apurv.studentassist.util.interfaces.LodingDialogInterface;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

public class HomeScreenActivity extends AppCompatActivity implements LodingDialogInterface {
    // private ActionBarControlMethods ActionBarControl = new ActionBarControlMethods();

    ListView drawerList;
    DrawerLayout drawerLayout;
    public CallbackManager callbackManager;
    ArrayList selectedUniversityIds = new ArrayList();

    SharedPreferences studentAssistSharedPreference;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public boolean dialog = false;


    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_home_screen);

        SharedPreferences sharedPreferences = getSharedPreferences(SAConstants.SHARED_PREFERENCE_NAME, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Intent intent = this.getIntent();


        //initializing toolbar
        Toolbar toolbar;
        toolbar = (Toolbar) findViewById(R.id.applicationBar);
        toolbar.setTitle(SAConstants.welcome);
        setSupportActionBar(toolbar);


        initializeFbSdk();


        //check if the user has logged in, else display login page
        if (AccessToken.getCurrentAccessToken() != null && !AccessToken.getCurrentAccessToken().isExpired()) {

            // coming from University Screen -> new user
            if (intent.getExtras() != null && intent.getExtras().getParcelableArrayList(SAConstants.UNIVERSITY_IDS) != null) {

                selectedUniversityIds = intent.getExtras().getParcelableArrayList(SAConstants.UNIVERSITY_IDS);

                editor.putString(SAConstants.UNIVERSITIES_IN_DB, SAConstants.YES);
                editor.commit();
                checkUserDetails();


                // Could be existing user -> to confirm lets check the Shared prefs for list of univs,
            } else {

                //check if the user has previous shared prefs for univ list in DB
                if (SAConstants.YES.equals(sharedPreferences.getString(SAConstants.UNIVERSITIES_IN_DB, ""))) {
                    checkUserDetails();
                } else {

                    //nothing in sharedPreferences so need to call DB
                    checkIfUserHasUnivsInDb();
                }


            }

        } else {

            //User not loggedin
            Intent FacebookLoginIntent = new Intent(this, FacebookLogin.class);
            startActivity(FacebookLoginIntent);
            finish();


        }


    }

    private void checkIfUserHasUnivsInDb() {


        StudentAssistBO studentAssistBo = new StudentAssistBO();
        UrlInterface urlgen = new UrlGenerator();
        LoadingDialog loadingDialog = Utilities.showLoadingDialog(SAConstants.SETTING_THINGS_UP, getSupportFragmentManager());
        studentAssistBo.volleyRequestWithLoadingDialog(urlgen.getUniversityDetailsForUser(), loadingDialog, null, Request.Method.GET);


    }

    private void initializeFbSdk() {
        //for FB SDK
        callbackManager = CallbackManager.Factory.create();

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.apurv.studentassist",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void checkUserDetails() {
        if (checkPlayServices()) {

            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            intent.putParcelableArrayListExtra(SAConstants.UNIVERSITY_IDS, selectedUniversityIds);

            this.startService(intent);

        }

    }


    /**
     * @param email
     * @param phone
     */
    public void emailPhoneCallback(String email, String phone) {


        SharedPreferences sharedPreferences = getSharedPreferences(SAConstants.SHARED_PREFERENCE_NAME, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();


        byte[] userInformationBytes = Base64.decode(sharedPreferences.getString(SAConstants.USER, ""), Base64.DEFAULT);
        User user = (User) ObjectSerializer.deserialize(userInformationBytes);

        user.setEmail(email);
        user.setPhoneNo(phone);

        editor.putString(SAConstants.USER, Base64.encodeToString(ObjectSerializer.serialize(user), Base64.DEFAULT));
        editor.commit();

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.

            Intent intent = new Intent(this, RegistrationIntentService.class);
            this.startService(intent);
        }


    }

    /**
     * @param view
     * @throws IOException
     */
    public void feedback(View view) throws IOException {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType(SAConstants.plainText);

        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{SAConstants.feedbackEmailAddress});
        intent.putExtra(Intent.EXTRA_SUBJECT, SAConstants.feedbackEmailSubject);
        startActivity(Intent.createChooser(intent, SAConstants.EmptyText));
    }

    /**
     * @param view
     */
    public void accommodation(View view) {
        Intent intent = new Intent(this, AccommodationActivity.class);
        intent.putExtra(SAConstants.LAUNCHED_FROM, SAConstants.HOME_SCREEN);
        startActivity(intent);
    }


    /**
     * @param view
     */
    public void courses(View view) {


        StudentAssistBO studentAssistBo = new StudentAssistBO();
        UrlInterface urlgen = new UrlGenerator();
        final HomeScreenActivity thisActivity = this;

        studentAssistBo.volleyRequest(urlgen.getUniversityDetailsForUser(), new NetworkInterface() {
            @Override
            public void onResponseUpdate(String jsonResponse) {

                L.m("json reponse" + jsonResponse);


                try {
                    Gson gson = new Gson();
                    List<University> universitiesList = gson.fromJson(jsonResponse, new TypeToken<List<University>>() {
                    }.getType());

                    // New User, ask him to choose universities
                    if (universitiesList.isEmpty()) {
                        Intent universitiesIntent = new Intent(thisActivity, UniversitiesListActivity.class);
                        startActivity(universitiesIntent);
                        finish();
                    } else {
                        for (University univ : universitiesList) {
                            selectedUniversityIds.add(univ.getUniversityId());
                        }

                        checkUserDetails();

                    }


                } catch (Exception e) {
                    ErrorReporting.logReport(e);
                }
            }


        }, null, Request.Method.GET);


    }

    /**
     * @param add
     */
    private void sendNotification(AccommodationAdd add) {       // creating intent to launch AdDetails, putting Accommodation Add in a parcel and passing it to intent

        Intent resultIntent = new Intent(this, AdDetailsActivity.class);
        Intent backIntent = new Intent(this, AccommodationActivity.class);
        resultIntent.putExtra(SAConstants.ACCOMMODATION_ADD_PARCELABLE, (Parcelable) add);
        backIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

        final PendingIntent resultPendingIntent = PendingIntent.getActivities(
                this, 0,
                new Intent[]{backIntent, resultIntent}, PendingIntent.FLAG_ONE_SHOT);


        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(SAConstants.STUDENT_ASSIST)
                .setContentText(SAConstants.NOTIFICATION_TEXT_APARTMENT)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(resultPendingIntent);


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    /**
     * @param view
     */
    public void airport(View view) {

        Intent intent = new Intent(this, AirportActivity.class);
        startActivity(intent);

    }


    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {

                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
            }
            return false;
        }
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_screen, menu);
        return true;


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:

                settings();
                return true;
            case R.id.action_logout:
                logout();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void settings() {

        Intent universitiesIntent = new Intent(this, UniversitiesListActivity.class);
        startActivity(universitiesIntent);
        finish();
    }

    private void logout() {

        SharedPreferences sharedPreferences = getSharedPreferences(SAConstants.SHARED_PREFERENCE_NAME, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear().commit();


        LoginManager.getInstance().logOut();
        Intent FacebookLoginIntent = new Intent(this, FacebookLogin.class);
        startActivity(FacebookLoginIntent);


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(SAConstants.DIALOG, dialog);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 0: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        L.m(resultCode + "");
    }

    //called after the loading dialog is dismissed
    @Override
    public void onResponse(String jsonResponse) {

        try {
            Gson gson = new Gson();
            List<University> universitiesList = gson.fromJson(jsonResponse, new TypeToken<List<University>>() {
            }.getType());

            // New User, ask him to choose universities
            if (!universitiesList.isEmpty()) {
                Intent universitiesIntent = new Intent(this, UniversitiesListActivity.class);
                startActivity(universitiesIntent);
                finish();
            } else {

                SharedPreferences sharedPreferences = getSharedPreferences(SAConstants.SHARED_PREFERENCE_NAME, 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(SAConstants.UNIVERSITIES_IN_DB, SAConstants.YES);
                editor.commit();
                checkUserDetails();

                checkUserDetails();

            }
        } catch (Exception e) {
            ErrorReporting.logReport(e);

            Bundle b = new Bundle();
            b.putString(SAConstants.ALERT_TEXT, "Something went wrong. Please try again later");
            AlertDialogL alertDialogL = new AlertDialogL();
            alertDialogL.setArguments(b);
            alertDialogL.show(getSupportFragmentManager(), "");


        }


    }
}