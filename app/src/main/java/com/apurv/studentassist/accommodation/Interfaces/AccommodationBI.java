package com.apurv.studentassist.accommodation.Interfaces;

import com.apurv.studentassist.accommodation.classes.AccommodationAdd;

import java.util.ArrayList;

/**
 * Created by apurv on 7/10/15.
 */
public interface AccommodationBI {

    public void onAccommodationAddsReady(ArrayList<AccommodationAdd> advertisements);

    public void onApartmentNamesReady(ArrayList<String> apartmentNames);
}
