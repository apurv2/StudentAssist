package com.apurv.studentassist.internet;

import android.support.v4.util.ArrayMap;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.apurv.studentassist.accommodation.Dialogs.LoadingDialog;
import com.apurv.studentassist.accommodation.classes.AccommodationAdd;
import com.apurv.studentassist.accommodation.classes.StudentAssistApplication;
import com.apurv.studentassist.util.L;
import com.apurv.studentassist.util.SAConstants;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;

import java.util.List;
import java.util.Map;

/**
 * Created by apurv on 6/7/15.
 * Purpose: Minimize the code to make a call to server again and again
 */
public class DatabaseManager {

    private List<AccommodationAdd> advertisements;
    NetworkInterface networkInterface;

    public void volleyGetRequest(String url, final NetworkInterface networkInterface) {

        L.m("url==" + url);
        L.m("volley request");

        this.networkInterface = networkInterface;
        RequestQueue requestQueue = Network.getNetworkInstnace().getRequestQueue();
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {

                L.m("response==" + response);
                networkInterface.onResponseUpdate(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                networkInterface.onResponseUpdate("Failure");

                Toast.makeText(StudentAssistApplication.getAppContext(), SAConstants.VOLLEY_ERROR, Toast.LENGTH_LONG).show();


            }
        });

        requestQueue.add(request);

    }

    public void volleyPostRequestWithLoadingDialog(String url, final LoadingDialog dialog, final String body) {
        L.m("POST url==" + url);
        L.m("POST BODY==" + body);

        RequestQueue requestQueue = Network.getNetworkInstnace().getRequestQueue();
        Map<String, String> mHeaders = new ArrayMap<String, String>();

        FacebookSdk.sdkInitialize(StudentAssistApplication.getAppContext());

        if (AccessToken.getCurrentAccessToken() != null && !AccessToken.getCurrentAccessToken().isExpired()) {
            String fbToken = AccessToken.getCurrentAccessToken().getToken();
            mHeaders.put(SAConstants.ACCESS_TOKEN, fbToken);
        }


        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                dialog.lodingDialogInterface.onResponse(response);
                dialog.dismiss();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(StudentAssistApplication.getAppContext(), SAConstants.VOLLEY_ERROR, Toast.LENGTH_LONG).show();
            }
        }) {
            public Map<String, String> getHeaders() {
                return mHeaders;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                return body.getBytes();
            }
        };

        requestQueue.add(request);


    }

    /**
     * Volley Post Request without Loading Dialog
     *
     * @param url
     * @param networkInterface
     * @param body
     */
    public void volleyPostRequest(String url, final NetworkInterface networkInterface, final String body) {
        L.m("POST url==" + url);
        L.m("POST BODY==" + body);

        RequestQueue requestQueue = Network.getNetworkInstnace().getRequestQueue();
        Map<String, String> mHeaders = new ArrayMap<String, String>();

        FacebookSdk.sdkInitialize(StudentAssistApplication.getAppContext());

        if (AccessToken.getCurrentAccessToken() != null && !AccessToken.getCurrentAccessToken().isExpired()) {
            String fbToken = AccessToken.getCurrentAccessToken().getToken();
            mHeaders.put(SAConstants.ACCESS_TOKEN, fbToken);
        }


        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                networkInterface.onResponseUpdate(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(StudentAssistApplication.getAppContext(), SAConstants.VOLLEY_ERROR, Toast.LENGTH_LONG).show();
            }
        }) {
            public Map<String, String> getHeaders() {
                return mHeaders;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                return body.getBytes();
            }
        };

        requestQueue.add(request);


    }


}
