package com.apurv.studentassist.internet;

import android.support.v4.util.ArrayMap;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.apurv.studentassist.accommodation.Dialogs.LoadingDialog;
import com.apurv.studentassist.accommodation.classes.StudentAssistApplication;
import com.apurv.studentassist.accommodation.urlInfo.UrlGenerator;
import com.apurv.studentassist.util.L;
import com.apurv.studentassist.util.SAConstants;
import com.facebook.FacebookSdk;

import java.util.Map;

/**
 * Created by apurv on 6/7/15.
 * Purpose: Minimize the code to make a call to server again and again
 */
public class StudentAssistBO {


    /**
     * @param url
     * @param dialog
     * @param body
     */
    public void volleyRequestWithLoadingDialog(String url, final LoadingDialog dialog, final String body, int method) {
        L.m("POST url==" + url);
        L.m("POST BODY==" + body);

        RequestQueue requestQueue = Network.getNetworkInstnace().getRequestQueue();
        Map<String, String> mHeaders = new ArrayMap<String, String>();
        FacebookSdk.sdkInitialize(StudentAssistApplication.getAppContext());

        UrlGenerator urlGenerator = new UrlGenerator();
        mHeaders.put(SAConstants.ACCESS_TOKEN, urlGenerator.getAccessToken());
        mHeaders.put("Content-Type", "application/json; charset=utf-8");

        L.m("token =="+ urlGenerator.getAccessToken());

        StringRequest request = new StringRequest(method, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                L.m("volley response=="+response);
                dialog.lodingDialogInterface.onResponse(response);
                dialog.dismiss();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(StudentAssistApplication.getAppContext(), SAConstants.VOLLEY_ERROR, Toast.LENGTH_LONG).show();
                dialog.lodingDialogInterface.onResponse("Failure");
                dialog.dismiss();


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
    public void volleyRequest(String url, final NetworkInterface networkInterface, final String body, int method) {
        L.m("POST url==" + url);
        L.m("POST BODY==" + body);

        RequestQueue requestQueue = Network.getNetworkInstnace().getRequestQueue();
        Map<String, String> mHeaders = new ArrayMap<String, String>();

        UrlGenerator urlGenerator = new UrlGenerator();
        mHeaders.put(SAConstants.ACCESS_TOKEN, urlGenerator.getAccessToken());
        mHeaders.put("Content-Type", "application/json; charset=utf-8");


        L.m("access Token =="+urlGenerator.getAccessToken());

        StringRequest request = new StringRequest(method, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                L.m("volley response=="+response);
                networkInterface.onResponseUpdate(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(StudentAssistApplication.getAppContext(), SAConstants.VOLLEY_ERROR, Toast.LENGTH_LONG).show();
                networkInterface.onResponseUpdate("Failure");

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
