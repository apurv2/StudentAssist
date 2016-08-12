package com.apurv.studentassist.notifications.receiver;

/**
 * Created by akamalapuri on 10/30/2015.
 */

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.apurv.studentassist.R;
import com.apurv.studentassist.accommodation.classes.StudentAssistApplication;
import com.apurv.studentassist.accommodation.urlInfo.UrlGenerator;
import com.apurv.studentassist.accommodation.urlInfo.UrlInterface;
import com.apurv.studentassist.notifications.business.NotificationBO;
import com.apurv.studentassist.notifications.interfaces.NotificationBI;
import com.apurv.studentassist.util.ErrorReporting;
import com.apurv.studentassist.util.L;
import com.apurv.studentassist.util.SAConstants;
import com.facebook.AccessToken;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

public class RegistrationIntentService extends IntentService {

    private static final String TAG = "RegIntentService";

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        try {

            InstanceID instanceID = InstanceID.getInstance(this);

            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            uploadUserDetails(token,instanceID.getId());

        } catch (Exception e) {
        }
    }


    private void uploadUserDetails(String registrationId,String instanceId) {



        SharedPreferences sharedPreferences = StudentAssistApplication.getAppContext().getSharedPreferences(SAConstants.sharedPreferenceName, 0);


        // setting GCM id to user and storing it into shared preferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SAConstants.GCM_ID, registrationId);
        editor.putString(SAConstants.INSTANCE_ID,instanceId);
        editor.commit();


        String fbToken = "";


        if (AccessToken.getCurrentAccessToken() != null && !AccessToken.getCurrentAccessToken().isExpired()) {

            fbToken = AccessToken.getCurrentAccessToken().getToken();
        }
        else
        {
            Toast.makeText(StudentAssistApplication.getAppContext(),"not registered",Toast.LENGTH_LONG).show();
        }


        UrlInterface urlGen = new UrlGenerator();
        try {

            String url = urlGen.createUser(fbToken,instanceId);

            new NotificationBO(new NotificationBI() {
                @Override
                public void onResponse(String response) {

                    L.m("successfully created user");
                }
            }, url);


        } catch (Exception e) {
            ErrorReporting.logReport(e);
        }

    }


}