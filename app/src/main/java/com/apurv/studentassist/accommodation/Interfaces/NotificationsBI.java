package com.apurv.studentassist.accommodation.Interfaces;

import com.apurv.studentassist.accommodation.classes.ApartmentNamesWithType;

import java.util.List;

/**
 * Created by akamalapuri on 7/23/2016.
 */
public interface NotificationsBI {

    void onApartmentNamesReady(List<ApartmentNamesWithType> apartmentNames);

}
