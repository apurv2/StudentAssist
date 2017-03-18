package com.apurv.studentassist.notifications.receiver;

/**
 * Created by akamalapuri on 10/30/2015.
 */

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;

import com.android.volley.Request;
import com.apurv.studentassist.R;
import com.apurv.studentassist.accommodation.classes.StudentAssistApplication;
import com.apurv.studentassist.accommodation.urlInfo.UrlGenerator;
import com.apurv.studentassist.accommodation.urlInfo.UrlInterface;
import com.apurv.studentassist.internet.NetworkInterface;
import com.apurv.studentassist.internet.StudentAssistBO;
import com.apurv.studentassist.util.ErrorReporting;
import com.apurv.studentassist.util.L;
import com.apurv.studentassist.util.SAConstants;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class RegistrationIntentService extends IntentService {

    private static final String TAG = "RegIntentService";
    ArrayList selectedUniversityIds;


    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        try {


            selectedUniversityIds = intent.getExtras().getParcelableArrayList(SAConstants.UNIVERSITY_IDS);

            InstanceID instanceID = InstanceID.getInstance(this);

            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            uploadUserDetails(token, instanceID.getId());

        } catch (Exception e) {
            ErrorReporting.logReport(e);
        }
    }


    private void uploadUserDetails(String registrationId, String instanceId) {


        SharedPreferences sharedPreferences = StudentAssistApplication.getAppContext().getSharedPreferences(SAConstants.SHARED_PREFERENCE_NAME, 0);


        // setting GCM id to user and storing it into shared preferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SAConstants.GCM_ID, registrationId);
        editor.putString(SAConstants.INSTANCE_ID, instanceId);
        editor.commit();
        UrlInterface urlGen = new UrlGenerator();
        try {

            String url = urlGen.createUser();
            Gson gson = new Gson();
            String createUserBody = gson.toJson(new CreateUser(instanceId, registrationId, selectedUniversityIds));

            StudentAssistBO studentAssistBO = new StudentAssistBO();
            studentAssistBO.volleyRequest(url, new NetworkInterface() {
                @Override
                public void onResponseUpdate(String jsonResponse) {

                    L.m("successfully created user");
                }
            }, createUserBody, Request.Method.PUT);

        } catch (Exception e) {
            ErrorReporting.logReport(e);
        }

    }

    private class CreateUser {
        String instanceId;
        String registrationId;
        List selectedUniversityIds;

        public CreateUser(String instanceId, String registrationId, List selectedUniversityIds) {
            this.instanceId = instanceId;
            this.registrationId = registrationId;
            this.selectedUniversityIds = selectedUniversityIds;
        }

    }


}