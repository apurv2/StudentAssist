package com.apurv.studentassist.accommodation.business.rules;

import android.widget.Toast;

import com.android.volley.Request;
import com.apurv.studentassist.accommodation.Dialogs.LoadingDialog;
import com.apurv.studentassist.accommodation.Interfaces.AccommodationBI;
import com.apurv.studentassist.accommodation.Interfaces.NotificationsBI;
import com.apurv.studentassist.accommodation.Interfaces.PostAccommodationBI;
import com.apurv.studentassist.accommodation.classes.AccommodationAdd;
import com.apurv.studentassist.accommodation.classes.ApartmentNamesWithType;
import com.apurv.studentassist.accommodation.classes.StudentAssistApplication;
import com.apurv.studentassist.internet.NetworkInterface;
import com.apurv.studentassist.internet.StudentAssistBO;
import com.apurv.studentassist.util.ErrorReporting;
import com.apurv.studentassist.util.L;
import com.apurv.studentassist.util.SAConstants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by apurv on 6/3/15.
 * Business Object to fetch and receive business information about Search Accommodation from backend
 */
public class AccommodationBO {
    AccommodationBI accommodationBI;
    PostAccommodationBI postAccommodationBI;


    StudentAssistBO studentAssistBO = new StudentAssistBO();

    public AccommodationBO() {
    }

    // to get accommodation Adds
    public AccommodationBO(String url, final AccommodationBI accommodationBI, String queryType) {

        L.m("server call made for query type" + queryType);


        this.accommodationBI = accommodationBI;


        if (queryType.equals(SAConstants.ACCOMMODATION_ADDS)) {

            studentAssistBO.volleyRequest(url, new NetworkInterface() {
                @Override
                public void onResponseUpdate(String jsonResponse) {


                    try {


                        Gson gson = new Gson();
                        ArrayList<AccommodationAdd> advertisements = gson.fromJson(jsonResponse, new TypeToken<List<AccommodationAdd>>() {}.getType());

                        accommodationBI.onAccommodationAddsReady(advertisements);


                    } catch (Exception e) {

                        ErrorReporting.logReport(e);
                        accommodationBI.onAccommodationAddsReady(new ArrayList());


                    }
                }


            }, null, Request.Method.GET);


        } else if (queryType.equals(SAConstants.APARTMENT_NAMES)) {

            final ArrayList<String> apartmentNames = new ArrayList<String>();

            studentAssistBO.volleyRequest(url, new NetworkInterface() {
                @Override
                public void onResponseUpdate(String jsonResponse) {


                    try {
                        JSONArray apartmentNamesJsonArray = new JSONArray(jsonResponse);
                        for (int counter = 0; counter < apartmentNamesJsonArray.length(); counter++) {
                            apartmentNames.add((apartmentNamesJsonArray.getJSONObject(counter)).getString(SAConstants.APARTMENT_NAME));

                        }

                        accommodationBI.onApartmentNamesReady(apartmentNames);


                    } catch (Exception e) {
                        e.printStackTrace();
                        accommodationBI.onApartmentNamesReady(new ArrayList());
                    }


                }


            }, null, Request.Method.GET);


        }


    }

    //Accommodation Business Object just returning response
    public AccommodationBO(String url, final PostAccommodationBI postaccommodationBI) {

        this.postAccommodationBI = postaccommodationBI;


        studentAssistBO.volleyRequest(url, new NetworkInterface() {
            @Override
            public void onResponseUpdate(String jsonResponse) {

                if (jsonResponse.equals(SAConstants.SUCCESS)) {
                    postaccommodationBI.onResponse(jsonResponse);
                }

            }
        }, null, Request.Method.POST);

    }

    public void getApartmentNamesWithType(String url, final NotificationsBI notificationsBI) {

        studentAssistBO.volleyRequest(url, new NetworkInterface() {
            @Override
            public void onResponseUpdate(String jsonResponse) {


                try {

                    List<ApartmentNamesWithType> apartmentNames = new ArrayList<ApartmentNamesWithType>();


                    JSONArray jArray = new JSONArray(jsonResponse);

                    L.m("json response ==" + jsonResponse);

                    if (jArray != null) {
                        for (int i = jArray.length() - 1; i >= 0; i--) {
                            JSONObject json_data = jArray.getJSONObject(i);

                            String apartmentName = json_data.getString(SAConstants.APARTMENT_NAME);
                            String apartmentType = json_data.getString(SAConstants.APARTMENT_TYPE);

                            apartmentNames.add(new ApartmentNamesWithType(apartmentName, apartmentType));
                        }

                        notificationsBI.onApartmentNamesReady(apartmentNames);

                    }


                } catch (Exception e) {
                    ErrorReporting.logReport(e);
                    notificationsBI.onApartmentNamesReady(new ArrayList());

                }


            }
        }, null, Request.Method.GET);

    }


    // Post Accommodation Add
    public AccommodationBO(String url, final LoadingDialog dialog) {


        studentAssistBO.volleyRequest(url, new NetworkInterface() {
            @Override
            public void onResponseUpdate(String jsonResponse) {

                if (jsonResponse.equals(SAConstants.SUCCESS)) {
                    dialog.lodingDialogInterface.onResponse(jsonResponse);
                    dialog.dismiss();

                } else {
                    Toast.makeText(StudentAssistApplication.getAppContext(), "There was an error, please try again", Toast.LENGTH_SHORT).show();
                }

            }
        }, null, Request.Method.DELETE);

    }


}
