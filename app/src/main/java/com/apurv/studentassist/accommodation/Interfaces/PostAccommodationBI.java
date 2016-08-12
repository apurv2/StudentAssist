package com.apurv.studentassist.accommodation.Interfaces;

import android.support.v4.app.DialogFragment;

/**
 * Created by akamalapuri on 10/13/2015.
 */
public interface PostAccommodationBI {

    void onResponse(String response);

    void onPostAddResponse(String response, DialogFragment dialogFragment);

}
