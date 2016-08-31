package com.apurv.studentassist.accommodation.activities;

/**
 * Created by akamalapuri on 9/24/2015.
 */

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;

import com.apurv.studentassist.R;
import com.apurv.studentassist.accommodation.classes.User;
import com.apurv.studentassist.util.ObjectSerializer;
import com.apurv.studentassist.util.SAConstants;
import com.apurv.studentassist.util.Utilities;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;


public class FacebookLogin extends AppCompatActivity {

    private CallbackManager callbackManager;
    private LoginButton loginButton;
    private AccessTokenTracker accessTokenTracker;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        Toolbar toolbar;
        toolbar = (Toolbar) findViewById(R.id.applicationBar);
        toolbar.setTitle("Welcome");
        setSupportActionBar(toolbar);

        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.login_button);


        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                Utilities.hideView(loginButton);
                login();
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });

        if (AccessToken.getCurrentAccessToken() != null && !AccessToken.getCurrentAccessToken().isExpired()) {
            Intent homeScreenIntent = new Intent(this, HomeScreenActivity.class);
            homeScreenIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK
                    | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(homeScreenIntent);
        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void login() {
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject graphUser,
                            GraphResponse response) {

                        try {


                            User user = new User(graphUser.getString(SAConstants.G_FIRST_NAME), graphUser.getString(SAConstants.G_LAST_NAME),
                                    null, null, graphUser.getString(SAConstants.G_ID));


                            SharedPreferences pref = getApplicationContext().getSharedPreferences(SAConstants.sharedPreferenceName, 0);
                            SharedPreferences.Editor editor = pref.edit();

                            editor.putString(SAConstants.USER, Base64.encodeToString(ObjectSerializer.serialize(user), Base64.DEFAULT));
                            editor.commit();

                            Intent homeScreenIntent = new Intent(getApplicationContext(),
                                    HomeScreenActivity.class);
                            homeScreenIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                                    | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    | Intent.FLAG_ACTIVITY_NEW_TASK);

                            startActivity(homeScreenIntent);
                            //   finish();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", SAConstants.G_FIRST_NAME + "," + SAConstants.G_ID + "," + SAConstants.G_LAST_NAME);
        request.setParameters(parameters);
        request.executeAsync();


    }


}
