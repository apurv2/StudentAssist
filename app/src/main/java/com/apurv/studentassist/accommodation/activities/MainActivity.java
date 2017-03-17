package com.apurv.studentassist.accommodation.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.apurv.studentassist.R;
import com.apurv.studentassist.util.L;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;

//import com.crashlytics.android.Crashlytics;

//import io.fabric.sdk.android.Fabric;

public class MainActivity extends Activity {

    private CallbackManager callbackManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);


        callbackManager = CallbackManager.Factory.create();




        if (AccessToken.getCurrentAccessToken() != null && !AccessToken.getCurrentAccessToken().isExpired()) {

          /*  Intent universitiesIntent = new Intent(this, UniversitiesListActivity.class);
            startActivity(universitiesIntent);
            finish();*/
            L.m("3");

            Intent homeScreenIntent = new Intent(this, HomeScreenActivity.class);
            homeScreenIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK
                    | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(homeScreenIntent);
        }
        else
        {
            L.m("4");
        }


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


}
