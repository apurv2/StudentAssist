package com.apurv.studentassist.accommodation.business.rules;

import android.widget.Toast;

import com.apurv.studentassist.accommodation.Dialogs.LoadingDialog;
import com.apurv.studentassist.accommodation.Interfaces.AccommodationBI;
import com.apurv.studentassist.accommodation.Interfaces.NotificationsBI;
import com.apurv.studentassist.accommodation.Interfaces.NotificationsInterface;
import com.apurv.studentassist.accommodation.Interfaces.PostAccommodationBI;
import com.apurv.studentassist.accommodation.Interfaces.RecentListInterface;
import com.apurv.studentassist.accommodation.classes.AccommodationAdd;
import com.apurv.studentassist.accommodation.classes.ApartmentNamesWithType;
import com.apurv.studentassist.accommodation.classes.NotificationSettings;
import com.apurv.studentassist.accommodation.classes.RecentListChecker;
import com.apurv.studentassist.accommodation.classes.StudentAssistApplication;
import com.apurv.studentassist.internet.DatabaseManager;
import com.apurv.studentassist.internet.NetworkInterface;
import com.apurv.studentassist.util.ErrorReporting;
import com.apurv.studentassist.util.L;
import com.apurv.studentassist.util.SAConstants;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by apurv on 6/3/15.
 * Business Object to fetch and receive business information about Search Accommodation from backend
 */
public class AccommodationBO {
    final ArrayList<AccommodationAdd> advertisements = new ArrayList<AccommodationAdd>();
    AccommodationBI accommodationBI;
    PostAccommodationBI postAccommodationBI;


    DatabaseManager databaseManager = new DatabaseManager();

    public AccommodationBO() {
    }

    // to get accommodation Adds
    public AccommodationBO(String url, final AccommodationBI accommodationBI, String queryType) {

        L.m("server call made for query type" + queryType);


        this.accommodationBI = accommodationBI;


        if (queryType.equals(SAConstants.ACCOMMODATION_ADDS)) {

            databaseManager.volleyGetRequest(url, new NetworkInterface() {
                @Override
                public void onResponseUpdate(String jsonResponse) {


                    try {

                        JSONArray jArray = new JSONArray(jsonResponse);
                        for (int i = jArray.length() - 1; i >= 0; i--) {
                            JSONObject json_data = jArray.getJSONObject(i);

                            String firstName, lastName, emailId, phoneNumber, apartmentName, vacancies, noOfRooms, lookingFor, cost, userId, addId, fbId, notes;
                            boolean userVisitedSw;

                            firstName = json_data.getString(SAConstants.FIRST_NAME);
                            lastName = json_data.getString(SAConstants.LAST_NAME);
                            emailId = json_data.has(SAConstants.EMAIL_ID) ? json_data.getString(SAConstants.EMAIL_ID) : "";
                            phoneNumber = json_data.has(SAConstants.PHONE_NUMBER) ? json_data.getString(SAConstants.EMAIL_ID) : "";
                            apartmentName = json_data.getString(SAConstants.APARTMENT_NAME);
                            vacancies = json_data.getString(SAConstants.VACANCIES);
                            noOfRooms = json_data.getString(SAConstants.NO_OF_ROOMS);
                            lookingFor = json_data.getString(SAConstants.GENDER);
                            cost = json_data.getString(SAConstants.COST);
                            userId = json_data.getString(SAConstants.USER_ID);
                            addId = json_data.getString(SAConstants.ADD_ID);
                            notes = json_data.getString(SAConstants.NOTES);
                            userVisitedSw=json_data.getBoolean(SAConstants.USER_VISITED_SW);



                            advertisements.add(new AccommodationAdd(firstName,
                                    lastName, emailId, phoneNumber, apartmentName,
                                    vacancies, lookingFor, noOfRooms, cost, userId,
                                    addId, notes,userVisitedSw));

                        }
                        accommodationBI.onAccommodationAddsReady(advertisements);


                    } catch (JSONException e) {

                        ErrorReporting.logReport(e);

                    } finally {

                    }


                }


            });


        } else if (queryType.equals(SAConstants.APARTMENT_NAMES)) {

            final ArrayList<String> apartmentNames = new ArrayList<String>();

            databaseManager.volleyGetRequest(url, new NetworkInterface() {
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
                    }


                }


            });


        }


    }

    //Accommodation Business Object just returning response
    public AccommodationBO(String url, final PostAccommodationBI postaccommodationBI) {

        this.postAccommodationBI = postaccommodationBI;


        databaseManager.volleyGetRequest(url, new NetworkInterface() {
            @Override
            public void onResponseUpdate(String jsonResponse) {

                if (jsonResponse.equals(SAConstants.SUCCESS)) {
                    postaccommodationBI.onResponse(jsonResponse);
                }

            }
        });

    }

    public AccommodationBO(String url, final RecentListInterface recentListInterface) {

        databaseManager.volleyGetRequest(url, new NetworkInterface() {
            @Override
            public void onResponseUpdate(String jsonResponse) {


                try {

                    List<RecentListChecker> recentList = new ArrayList<RecentListChecker>();


                    JSONArray jArray = new JSONArray(jsonResponse);

                    L.m("json response ==" + jsonResponse);

                    if (jArray != null) {
                        for (int i = jArray.length() - 1; i >= 0; i--) {
                            JSONObject json_data = jArray.getJSONObject(i);

                            String addId = json_data.getString(SAConstants.ADD_ID);
                            recentList.add(new RecentListChecker(addId));
                        }

                        recentListInterface.recentlyVisitedAdvertisements(recentList);

                    }


                } catch (Exception e) {
                    ErrorReporting.logReport(e);
                }


            }
        });


    }

    public void getApartmentNamesWithType(String url, final NotificationsBI notificationsBI) {

        databaseManager.volleyGetRequest(url, new NetworkInterface() {
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
                }


            }
        });

    }


    // Post Accommodation Add
    public AccommodationBO(String url, final LoadingDialog dialog) {


        databaseManager.volleyGetRequest(url, new NetworkInterface() {
            @Override
            public void onResponseUpdate(String jsonResponse) {

                if (jsonResponse.equals(SAConstants.SUCCESS)) {


                    dialog.lodingDialogInterface.onResponse(jsonResponse);
                    dialog.dismiss();

                } else {
                    Toast.makeText(StudentAssistApplication.getAppContext(), "There was an error, please try again", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }


    // Notification Settings
    public void getNotificationSettings(String url, final NotificationsInterface notificationsInterface) {

        databaseManager.volleyGetRequest(url, new NetworkInterface() {
            @Override
            public void onResponseUpdate(String jsonResponse) {

                Gson gson = new Gson();
                NotificationSettings settings = new NotificationSettings();
                settings = gson.fromJson(jsonResponse, NotificationSettings.class);

                notificationsInterface.onResponse(settings);


            }
        });

    }


}
