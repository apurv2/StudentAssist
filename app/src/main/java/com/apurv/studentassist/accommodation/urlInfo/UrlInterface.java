package com.apurv.studentassist.accommodation.urlInfo;

import java.io.UnsupportedEncodingException;

/**
 * Created by apurv on 6/7/15.
 */
public interface UrlInterface {

    public String getApartmentNamesUrl(String aptType);


    public String getSearchAccommodationAdds(String leftSpinner, String rightSpinner) throws UnsupportedEncodingException;

    public String getAdvancedSearchAccommodationAdds(String apartmentName, String gender) throws UnsupportedEncodingException;

    public String getUserPosts(String userId) throws UnsupportedEncodingException;

    public String getAddNewAptUrl(String apartmentName, String apartmentType) throws UnsupportedEncodingException;

    public String getPostAccUrl() throws UnsupportedEncodingException;

    public String getNotificationSettingsUrl(String access_token)
            throws UnsupportedEncodingException;


    public String getSubscribeNotificationsUrl()
            throws UnsupportedEncodingException;


    public String createUser();

    public String getDeleteAccommodationPostUrl(String addId) throws UnsupportedEncodingException;

    public String getAirportUrl() throws UnsupportedEncodingException;

    public String getRecentlyViewed() throws UnsupportedEncodingException;

    public String getApartmentNamesWithTypeUrl() throws UnsupportedEncodingException;

    public String setUserVisitedAdds() throws UnsupportedEncodingException;

    public String unSubscribeNotifications() throws UnsupportedEncodingException;

    public String getUserNotificationsUrl() throws UnsupportedEncodingException;

    public String getAccessToken();


}
