package com.apurv.studentassist.airport.business.rules;

import com.apurv.studentassist.airport.classes.AirportService;
import com.apurv.studentassist.airport.interfaces.AirportInterface;
import com.apurv.studentassist.internet.DatabaseManager;
import com.apurv.studentassist.internet.NetworkInterface;
import com.apurv.studentassist.util.ErrorReporting;
import com.apurv.studentassist.util.L;
import com.apurv.studentassist.util.SAConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by apurv on 6/3/15.
 * Business Object to fetch and receive business information about Search Accommodation from backend
 */
public class AirportBO {


    DatabaseManager databaseManager = new DatabaseManager();


    // Notification Settings
    public AirportBO(String url, final AirportInterface arportInterface) {


        databaseManager.volleyGetRequest(url, new NetworkInterface() {
            @Override
            public void onResponseUpdate(String jsonResponse) {

                try {

                    L.m("response=" + jsonResponse);

                    List<AirportService> services = new ArrayList<AirportService>();
                    JSONArray jArray = new JSONArray(jsonResponse);

                    for (int i = jArray.length() - 1; i >= 0; i--) {
                        JSONObject json_data = jArray.getJSONObject(i);

                        String serviceName, description, link;

                        serviceName = json_data.getString(SAConstants.GROUP_NAME);
                        description = json_data.getString(SAConstants.GROUP_DESCRIPTION);
                        link = json_data.getString(SAConstants.GROUP_LINK);


                        services.add(new AirportService(serviceName, description, link));

                    }
                    arportInterface.onResponse(services);


                } catch (JSONException e) {

                    ErrorReporting.logReport(e);

                } finally {

                }
            }
        });

    }


}
