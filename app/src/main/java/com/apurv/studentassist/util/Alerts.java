package com.apurv.studentassist.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by akamalapuri on 10/15/2015.
 */
public class Alerts {

    public static Map<Integer, String> errors = new HashMap<Integer, String>();
    public static Map<Integer, String> alerts = new HashMap<Integer, String>();

    static {
        errors.put(1, "Important Message!!!");
        errors.put(2, "Please make sure that your internet connection is up!");
        errors.put(3, "Please make sure that you have filled in all the fields and try again!");

        errors.put(4, "Cost of living cannot be");
        errors.put(5, "Cost of living cannot be EMPTY!");
        errors.put(6, "Sorry we could not process your request at this time!!");
        errors.put(7, "Please enter a valid facebook Id!");
        errors.put(8, "Only letters and numbers are acceptable in text box!");
        errors.put(9, "Phone number or email address cannot be null!");
        errors.put(10, "Please enter a valid phone Number!");
        errors.put(11, "Please dont forget to add the country code!");
        errors.put(12, "Please grant call permissions and try again!");
        errors.put(13, "Gender Cannot be empty");
        errors.put(14, "Apartment Types cannot be empty");
        errors.put(15, "Something went wrong, please try again later");



    }

    static {
        alerts.put(1062, "You have already subscribed to notifications in this search criteria.");
        alerts.put(0, "Subscription successful !! you can disable this in settings.");
    }


}
