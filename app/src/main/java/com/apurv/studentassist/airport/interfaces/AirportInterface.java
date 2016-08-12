package com.apurv.studentassist.airport.interfaces;

import com.apurv.studentassist.airport.classes.AirportService;

import java.util.List;

/**
 * Created by akamalapuri on 10/13/2015.
 */
public interface AirportInterface {

    void onResponse(List<AirportService> services);

}
