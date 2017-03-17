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

import com.apurv.studentassist.R;
import com.apurv.studentassist.accommodation.classes.AccommodationAdd;
import com.apurv.studentassist.accommodation.classes.User;
import com.apurv.studentassist.airport.activities.AirportActivity;
import com.apurv.studentassist.appInfo.dialogs.EmailPhone;
import com.apurv.studentassist.notifications.receiver.RegistrationIntentService;
import com.apurv.studentassist.util.L;
import com.apurv.studentassist.util.ObjectSerializer;
import com.apurv.studentassist.util.SAConstants;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.ArrayList;

public class HomeScreenActivity extends AppCompatActivity {
    // private ActionBarControlMethods ActionBarControl = new ActionBarControlMethods();

    ListView drawerList;
    DrawerLayout drawerLayout;
    public CallbackManager callbackManager;

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

        Toolbar toolbar;
        toolbar = (Toolbar) findViewById(R.id.applicationBar);
        toolbar.setTitle(SAConstants.welcome);
        setSupportActionBar(toolbar);


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

        if (AccessToken.getCurrentAccessToken() != null && !AccessToken.getCurrentAccessToken().isExpired()) {

            if (true) {

                Intent universitiesIntent = new Intent(this, UniversitiesListActivity.class);
                startActivity(universitiesIntent);
                finish();
            }


            if (savedInstanceState != null) {
                if (!savedInstanceState.getBoolean(SAConstants.DIALOG)) {
                    checkUserDetails();
                }
            } else {
                checkUserDetails();
            }


            L.m("valid token");
        } else {

          /*  L.m("invalid token");
            DialogFragment facebookLoginDialog = new FacebookLoginDialog();
            facebookLoginDialog.show(getSupportFragmentManager(), "");*/

            Intent FacebookLoginIntent = new Intent(this, FacebookLogin.class);
            startActivity(FacebookLoginIntent);
            finish();


        }


    }


    private void checkUserDetails() {
        if (checkPlayServices()) {

            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
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

        String firstName, lastName, emailId, phoneNumber, ApartmentName, vacancies, NoOfRooms, lookingFor, cost, userId, addId, notes;
        boolean userVisitedSw;


        firstName = SAConstants.FIRST_NAME;
        lastName = SAConstants.LAST_NAME;
        emailId = SAConstants.EMAIL_ID;
        phoneNumber = SAConstants.PHONE_NUMBER;
        ApartmentName = SAConstants.APARTMENT_NAME;
        vacancies = SAConstants.VACANCIES;
        NoOfRooms = SAConstants.NO_OF_ROOMS;
        lookingFor = SAConstants.GENDER;
        cost = SAConstants.COST;
        userId = "1118294135";
        addId = "5";
        notes = SAConstants.NOTES;
        userVisitedSw = false;


        sendNotification(new AccommodationAdd(firstName, lastName, emailId,
                phoneNumber, ApartmentName, vacancies, NoOfRooms, lookingFor,
                cost, userId, addId, notes, userVisitedSw, new ArrayList<String>()));

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

        SharedPreferences sharedPreferences = getSharedPreferences(SAConstants.SHARED_PREFERENCE_NAME, 0);
        byte[] userInformationBytes = Base64.decode(sharedPreferences.getString(SAConstants.USER, ""), Base64.DEFAULT);
        User user = (User) ObjectSerializer.deserialize(userInformationBytes);


        Bundle bundle = new Bundle();
        bundle.putBoolean(SAConstants.FETCH, true);
        bundle.putString(SAConstants.EMAIL_ID, user.getEmail());
        bundle.putString(SAConstants.PHONE_NUMBER, user.getPhoneNo());

        EmailPhone emailPhone = new EmailPhone();
        emailPhone.setArguments(bundle);
        emailPhone.show(getSupportFragmentManager(), "");
    }

    private void logout() {

        SharedPreferences sharedPreferences = getSharedPreferences(SAConstants.SHARED_PREFERENCE_NAME, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();


        LoginManager.getInstance().logOut();
        Intent FacebookLoginIntent = new Intent(this, FacebookLogin.class);
        startActivity(FacebookLoginIntent);
        finish();


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

}