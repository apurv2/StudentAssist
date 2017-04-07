package com.apurv.studentassist.accommodation.urlInfo;

import com.apurv.studentassist.accommodation.classes.StudentAssistApplication;
import com.apurv.studentassist.util.ErrorReporting;
import com.apurv.studentassist.util.SAConstants;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
    public static final String GET_USER_NOTIFICATIONS = "accommodation/getAccommodationNotifications";
    public static final String GET_NOTIFICATION_SETTINGS = "accommodation/getNotificationSettings";
    public static final String INSERT_NOTIFICATIONS = "accommodation/subscribeNotifications";
    public static final String CREATE_USERS = "accommodation/createUser";
    public static final String DELETE_ACCOMMODATION = "accommodation/deleteAccommodationAdd";
    public static final String AIRPORT = "airportService/getAllServices";
    public static final String RECENTLY_VIEWED = "accommodation/getRecentlyViewed";
    public static final String GET_ALL_APARTMENT_NAMES = "accommodation/getAllApartmentNames";
    public static final String GET_APARTMENT_NAMES = "accommodation/getApartmentNames";
    public static final String GET_APARTMENTNAMES_WITH_TYPE = "accommodation/getAllApartmentsWithType";
    public static final String SET_USER_VISITED_ADDS = "accommodation/setUserVisitedAdds";
    public static final String UNSUBSCRIBE_NOTIFICATIONS = "accommodation/unSubscribeNotifications";
    public static final String GET_UNIVERSITY_NAMES = "accommodation/getAllUniversitiesList";
    public static final String GET_UNIVERSITY_DETAILS_FOR_USER = "accommodation/getUniversityDetailsForUser";
    public static final String GET_UNIVERSITY_NAMES_FOR_USER = "accommodation/getUniversityNamesWithId";


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
    public String getAccessToken() {


        FacebookSdk.sdkInitialize(StudentAssistApplication.getAppContext());
        if (AccessToken.getCurrentAccessToken() != null && !AccessToken.getCurrentAccessToken().isExpired()) {
            String fbToken = AccessToken.getCurrentAccessToken().getToken();
            return fbToken;
        }
        return "";


    }

    @Override
    public String getUniversitieListUrl() {

        return SAConstants.URL + "/" + GET_UNIVERSITY_NAMES;

    }

    @Override
    public String getUniversityDetailsForUser() {
        return SAConstants.URL + "/" + GET_UNIVERSITY_DETAILS_FOR_USER;

    }

    @Override
    public String getUniversityNamesForUser() {
        return SAConstants.URL + "/" + GET_UNIVERSITY_NAMES_FOR_USER;
    }


    @Override
    public String getApartmentNamesUrl(String aptType) {

        String url = "";
        if (aptType.equals(SAConstants.ALL)) {
            url = SAConstants.URL + "/" + GET_ALL_APARTMENT_NAMES;
        } else {

            url = SAConstants.URL + "/" + GET_APARTMENT_NAMES + "?" +
                    SAConstants.APARTMENT_TYPE + "=" + apartmentTypeCodeMap.get(aptType);
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


        parameters = SAConstants.LEFT_SPINNER + "=" + leftSpinnerCodeMap.get(leftSpinner) + "&" +
                SAConstants.RIGHT_SPINNER + "=" + URLEncoder.encode(queryparam, "UTF-8") + "&position=1";

        url = SAConstants.URL + "/" + GET_ACCOMMODATION_ADDS + "?" + parameters;


        return url;


    }


    @Override
    public String getAdvancedSearchAccommodationAdds(String apartmentName, String gender) throws UnsupportedEncodingException {

        String url = "", parameters = "";

        parameters = SAConstants.APARTMENT_NAME + "=" + URLEncoder.encode(apartmentName, "UTF-8") + "&"
                + SAConstants.GENDER + "=" + URLEncoder.encode(gender, "UTF-8") + "&position=1";

        url = SAConstants.URL + "/" + GET_ADVANCED_SEARCH_ADDS + "?" + parameters;


        return url;
    }

    @Override
    public String getUserPosts(String userId) throws UnsupportedEncodingException {
        return SAConstants.URL + "/" + GET_USER_POSTS;
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
    public String getPostAccUrl() throws UnsupportedEncodingException {
        return SAConstants.URL + "/" + POST_ACCOMMODATION;//+ "?" + parameters;
    }

    @Override
    public String getNotificationSettingsUrl(String access_token) throws UnsupportedEncodingException {
        String url = "", parameters = "";

        parameters = SAConstants.ACCESS_TOKEN + "=" + URLEncoder.encode(access_token, "UTF-8");

        url = SAConstants.URL + "/" + GET_NOTIFICATION_SETTINGS + "?" + parameters;

        return url;
    }


    @Override
    public String createUser() {
        try {
            return SAConstants.URL + "/" + CREATE_USERS;

        } catch (Exception e) {
            ErrorReporting.logReport(e);
            return "";
        }
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
    public String getRecentlyViewed() throws UnsupportedEncodingException {
        String url = "";

        url = SAConstants.URL + "/" + RECENTLY_VIEWED + "?position=1";
        return url;
    }

    /**
     * POST REQUEST, the token is already in the body
     *
     * @return
     * @throws UnsupportedEncodingException
     */
    @Override
    public String getSubscribeNotificationsUrl() throws UnsupportedEncodingException {
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
    public String getApartmentNamesWithTypeUrl() throws UnsupportedEncodingException {


        String url = SAConstants.URL + "/" + GET_APARTMENTNAMES_WITH_TYPE;
        return url;
    }

    /**
     * POST REQUEST, the token is already in the body
     *
     * @return
     * @throws UnsupportedEncodingException
     */
    @Override
    public String setUserVisitedAdds() throws UnsupportedEncodingException {

        String url = SAConstants.URL + "/" + SET_USER_VISITED_ADDS;
        return url;
    }

    /**
     * POST REQUEST, the token is already in the body
     *
     * @return
     * @throws UnsupportedEncodingException
     */
    @Override
    public String unSubscribeNotifications() throws UnsupportedEncodingException {

        String url = SAConstants.URL + "/" + UNSUBSCRIBE_NOTIFICATIONS;
        return url;


    }

    @Override
    public String getUserNotificationsUrl() throws UnsupportedEncodingException {

        String url = SAConstants.URL + "/" + GET_USER_NOTIFICATIONS + "?position=1";
        return url;
    }


    public static String getProfilePictureURL(String id) {
        String url = "https://graph.facebook.com/" + id + "/picture?type=large";

        return url;
    }

    public static String getPaginationUrl(String url, int position) {

        String preUrl;
        String params;
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        if (url.contains("?")) {
            preUrl = url.split("\\?")[0];
            params = url.split("\\?")[1];
            map = (LinkedHashMap<String, String>) getQueryMap(params);

        } else {
            preUrl = url + "?";
        }

        if (map.containsKey(SAConstants.POSITION)) {

            map.remove(SAConstants.POSITION);
        }
        map.put(SAConstants.POSITION, String.valueOf(position));

        return frameUrlFromMap(preUrl, map);


    }

    private static String frameUrlFromMap(String preUrl, LinkedHashMap<String, String> map) {

        try {
            StringBuffer url = new StringBuffer(preUrl + "?");

            for (String key : map.keySet()) {
                url.append(key);
                url.append("=");
                url.append(map.get(key));
                url.append("&");


            }
            url.deleteCharAt(url.length() - 1);

            return url.toString();
        } catch (Exception e) {
            ErrorReporting.logReport(e);
        }
        return "";
    }

    public static Map<String, String> getQueryMap(String query) {
        String[] params = query.split("&");
        Map<String, String> map = new LinkedHashMap<String, String>();
        for (String param : params) {
            String name = param.split("=")[0];
            String value = param.split("=")[1];
            map.put(name, value);
        }
        return map;
    }

    public static Map<String, String> getUrlFromMap(Map queryMap) {


        return null;

    }

}
