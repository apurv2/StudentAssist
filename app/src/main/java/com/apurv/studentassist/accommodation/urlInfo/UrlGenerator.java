package com.apurv.studentassist.accommodation.urlInfo;

import com.apurv.studentassist.accommodation.classes.StudentAssistApplication;
import com.apurv.studentassist.util.ErrorReporting;
import com.apurv.studentassist.util.SAConstants;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by apurv on 6/7/15.
 */


public class UrlGenerator implements UrlInterface {


    public static final String GET_ACCOMMODATION_ADDS = "accommodation/getSimpleSearchAdds";
    public static final String GET_ADVANCED_SEARCH_ADDS = "accommodation/getAdvancedAdvertisements";
    public static final String GET_USER_POSTS = "accommodation/getUserPosts";
    public static final String ADD_NEW_APT = "accommodation/addNewApartment";
    public static final String POST_ACCOMMODATION = "accommodation/createAccommodationAdd";
    public static final String GET_NOTIFICATION_SETTINGS = "accommodation/getNotificationSettings";
    public static final String DELETE_NOTIFICATION_REQUESTS = "accommodation/deleteNotificationSetting";
    public static final String INSERT_NOTIFICATIONS = "accommodation/subscribeNotifications";
    public static final String CREATE_USERS = "accommodation/createUser";
    public static final String DELETE_ACCOMMODATION = "accommodation/deleteAccommodationAdd";
    public static final String AIRPORT = "airportService/getAllServices";
    public static final String RECENT_LIST_CHECKER = "accommodation/recentListChecker";
    public static final String GET_ALL_APARTMENT_NAMES = "accommodation/getAllApartmentNames";
    public static final String GET_APARTMENT_NAMES = "accommodation/getApartmentNames";
    public static final String GET_APARTMENTNAMES_WITH_TYPE = "accommodation/getAllApartmentsWithType";
    public static final String SET_USER_VISITED_ADDS = "accommodation/setUserVisitedAdds";


    private static final Map<String, String> apartmentTypeCodeMap;
    private static final Map<String, String> leftSpinnerCodeMap;

    public static Map<String, String> getApartmentTypeCodeMap() {
        return apartmentTypeCodeMap;
    }

    static {

        apartmentTypeCodeMap = new HashMap<String, String>();
        apartmentTypeCodeMap.put("On-Campus", "on");
        apartmentTypeCodeMap.put("Off-Campus", "off");
        apartmentTypeCodeMap.put("Dorms", "dorms");

        leftSpinnerCodeMap = new HashMap<>();
        leftSpinnerCodeMap.put("Apartment Type", SAConstants.APARTMENT_TYPE);
        leftSpinnerCodeMap.put("Apartment Name", SAConstants.APARTMENT_NAME);
        leftSpinnerCodeMap.put("Gender", SAConstants.GENDER);

    }


    @Override
    public String getApartmentNamesUrl(String aptType) {

        String url = "";
        if (aptType.equals(SAConstants.ALL)) {
            url = SAConstants.URL + "/" + GET_ALL_APARTMENT_NAMES;
        } else {

            url = SAConstants.URL + "/" + GET_APARTMENT_NAMES + "?" + SAConstants.APARTMENT_TYPE + "=" + apartmentTypeCodeMap.get(aptType);
        }


        return url;
    }


    // Simple search accommodation adds
    @Override
    public String getSearchAccommodationAdds(String leftSpinner, String rightSpinner) throws UnsupportedEncodingException {
        String url = "";
        String parameters = "";
        String queryparam = "";

        queryparam = apartmentTypeCodeMap.get(rightSpinner) == null ? rightSpinner : apartmentTypeCodeMap.get(rightSpinner);

        FacebookSdk.sdkInitialize(StudentAssistApplication.getAppContext());

        if (AccessToken.getCurrentAccessToken() != null && !AccessToken.getCurrentAccessToken().isExpired()) {

            String fbToken = AccessToken.getCurrentAccessToken().getToken();
            parameters = SAConstants.LEFT_SPINNER + "=" + leftSpinnerCodeMap.get(leftSpinner) + "&" +
                    SAConstants.RIGHT_SPINNER + "=" + URLEncoder.encode(queryparam, "UTF-8") + "&" + SAConstants.ACCESS_TOKEN + "=" + fbToken;

            url = SAConstants.URL + "/" + GET_ACCOMMODATION_ADDS + "?" + parameters;


            return url;

        } else {
            return "";
        }


    }

    @Override
    public String getAdvancedSearchAccommodationAdds(String apartmentName, String gender) throws UnsupportedEncodingException {

        String url = "", parameters = "";

        parameters = SAConstants.APARTMENT_NAME + "=" + URLEncoder.encode(apartmentName, "UTF-8") + "&"
                + SAConstants.GENDER + "=" + URLEncoder.encode(gender, "UTF-8");

        url = SAConstants.URL + "/" + GET_ADVANCED_SEARCH_ADDS + "?" + parameters;


        return url;
    }

    @Override
    public String getUserPosts(String userId) throws UnsupportedEncodingException {

        String url = "", parameters = "";


        parameters = SAConstants.USER_ID + "=" + URLEncoder.encode(userId, "UTF-8");

        url = SAConstants.URL + "/" + GET_USER_POSTS + "?" + parameters;
        return url;
    }

    @Override
    public String getAddNewAptUrl(String apartmentName, String apartmentType) throws UnsupportedEncodingException {

        String url = "", parameters = "", queryparam = "";

        queryparam = apartmentTypeCodeMap.get(apartmentType) == null ? "" : apartmentTypeCodeMap.get(apartmentType);


        parameters = SAConstants.APARTMENT_NAME + "=" + URLEncoder.encode(apartmentName, "UTF-8") + "&" + SAConstants.APARTMENT_TYPE + "=" +
                URLEncoder.encode(queryparam, "UTF-8");

        url = SAConstants.URL + "/" + ADD_NEW_APT + "?" + parameters;
        return url;
    }

    @Override
    public String getPostAccUrl(String apartmentName, String noOfRooms,
                                String noOfVacancies, String lookingFor, String userId, String cost, String notes
    ) throws UnsupportedEncodingException {


        String url = "", parameters = "";

        FacebookSdk.sdkInitialize(StudentAssistApplication.getAppContext());

        if (AccessToken.getCurrentAccessToken() != null && !AccessToken.getCurrentAccessToken().isExpired()) {
            String fbToken = AccessToken.getCurrentAccessToken().getToken();


            parameters = SAConstants.APARTMENT_NAME + "=" + URLEncoder.encode(apartmentName, "UTF-8") + "&"
                    + SAConstants.NO_OF_ROOMS + "=" + URLEncoder.encode(noOfRooms, "UTF-8") + "&"
                    + SAConstants.VACANCIES + "=" + URLEncoder.encode(noOfVacancies, "UTF-8") + "&"
                    + SAConstants.COST + "=" + URLEncoder.encode(cost, "UTF-8") + "&"
                    + SAConstants.GENDER + "=" + URLEncoder.encode(lookingFor, "UTF-8") + "&"
                    + SAConstants.ACCESS_TOKEN + "=" + URLEncoder.encode(fbToken, "UTF-8") + "&"
                    + SAConstants.NOTES + "=" + URLEncoder.encode(notes, "UTF-8") + "&"
                    + SAConstants.USER_ID + "=" + URLEncoder.encode(userId, "UTF-8");


            url = SAConstants.URL + "/" + POST_ACCOMMODATION + "?" + parameters;

        }
        return url;
    }

    @Override
    public String getNotificationSettingsUrl(String access_token) throws UnsupportedEncodingException {
        String url = "", parameters = "";

        parameters = SAConstants.ACCESS_TOKEN + "=" + URLEncoder.encode(access_token, "UTF-8");

        url = SAConstants.URL + "/" + GET_NOTIFICATION_SETTINGS + "?" + parameters;

        return url;
    }

    @Override
    public String getDeleteNotificationSettingUrl(int notificationId) throws UnsupportedEncodingException {
        String url = "", parameters = "";

        try {

            parameters = SAConstants.NOTIFICATIONID + "=" + URLEncoder.encode(notificationId + "", "UTF-8");
            url = SAConstants.URL + "/" + DELETE_NOTIFICATION_REQUESTS + "?" + parameters;

        } catch (Exception e) {
            ErrorReporting.logReport(e);
            return "";
        }


        return url;
    }

    @Override
    public String getInsertNotificationsUrl() throws UnsupportedEncodingException {
        String url = "", parameters = "";

        try {

            url = SAConstants.URL + "/" + INSERT_NOTIFICATIONS;

        } catch (Exception e) {
            ErrorReporting.logReport(e);
            return "";
        }


        return url;


    }

    @Override
    public String createUser(String accessToken, String instanceId) {

        String url = "", parameters = "";

        try {

            parameters = SAConstants.ACCESS_TOKEN + "=" + URLEncoder.encode(accessToken + "", "UTF-8") + "&" +
                    SAConstants.INSTANCE_ID + "=" + URLEncoder.encode(instanceId + "", "UTF-8");


            url = SAConstants.URL + "/" + CREATE_USERS + "?" + parameters;

        } catch (Exception e) {
            ErrorReporting.logReport(e);
            return "";
        }


        return url;
    }


    @Override
    public String getDeleteAccommodationPostUrl(String addId) throws UnsupportedEncodingException {


        String url = "", parameters = "";

        parameters = SAConstants.ADD_ID + "=" + URLEncoder.encode(addId + "", "UTF-8");

        url = SAConstants.URL + "/" + DELETE_ACCOMMODATION + "?" + parameters;


        return url;
    }

    @Override
    public String getAirportUrl() throws UnsupportedEncodingException {

        String url = SAConstants.URL + "/" + AIRPORT;
        return url;
    }

    @Override
    public String getRecentListCheckerUrl(String json) throws UnsupportedEncodingException {


        String parameters = SAConstants.ADD_ID + "=" + URLEncoder.encode(json, "UTF-8");

        String url = SAConstants.URL + "/" + RECENT_LIST_CHECKER + "?" + parameters;


        return url;
    }

    @Override
    public String getApartmentNamesWithTypeUrl() throws UnsupportedEncodingException {


        String url = SAConstants.URL + "/" + GET_APARTMENTNAMES_WITH_TYPE;
        return url;
    }

    @Override
    public String setUserVisitedAdds() throws UnsupportedEncodingException {

        String url = SAConstants.URL + "/" + SET_USER_VISITED_ADDS;
        return url;
    }


    public static String getProfilePictureURL(String id) {
        String url = "https://graph.facebook.com/" + id + "/picture?type=large";

        return url;
    }


}
