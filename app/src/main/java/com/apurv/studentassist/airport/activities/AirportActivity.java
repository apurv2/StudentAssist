
package com.apurv.studentassist.airport.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.apurv.studentassist.R;
import com.apurv.studentassist.accommodation.urlInfo.UrlGenerator;
import com.apurv.studentassist.accommodation.urlInfo.UrlInterface;
import com.apurv.studentassist.airport.adapters.AirportAdapter;
import com.apurv.studentassist.airport.business.rules.AirportBO;
import com.apurv.studentassist.airport.classes.AirportService;
import com.apurv.studentassist.airport.interfaces.AirportInterface;
import com.apurv.studentassist.airport.interfaces.RecyclerTouchInterface;
import com.apurv.studentassist.util.ErrorReporting;
import com.apurv.studentassist.util.SAConstants;
import com.apurv.studentassist.util.Utilities;

import java.util.ArrayList;
import java.util.List;

public class AirportActivity extends AppCompatActivity implements RecyclerTouchInterface {

    private AirportAdapter mAirportAdapter;
    private RecyclerView mRecyclerVIew;
    UrlInterface urlGen = new UrlGenerator();
    List<AirportService> services = new ArrayList<AirportService>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_airport);

        Toolbar toolbar;
        toolbar = (Toolbar) findViewById(R.id.applicationBar);
        toolbar.setTitle(SAConstants.USER_POSTS);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        setmRecyclerVIew();

        if (savedInstanceState != null) {
            populateRecyclerView(savedInstanceState.<AirportService>getParcelableArrayList(SAConstants.STATE_CHANGED));

        } else {
            getFromServer();
        }

    }

    private void populateRecyclerView(List<AirportService> services1) {

        Utilities.hideView(findViewById(R.id.loadingPanel));
        services.addAll(services1);

        mAirportAdapter.clear();
        mAirportAdapter.addAll(services);
        mAirportAdapter.notifyDataSetChanged();

    }


    public void setmRecyclerVIew() {


        mAirportAdapter = new AirportAdapter(getApplicationContext(), new ArrayList<AirportService>(), this);

        mRecyclerVIew = (RecyclerView) findViewById(R.id.servicesList);
        mRecyclerVIew.setAdapter(mAirportAdapter);


        mRecyclerVIew.setLayoutManager(new LinearLayoutManager(getApplicationContext()));


    }

    public void getFromServer() {

        try {

            String url = urlGen.getAirportUrl();

            new AirportBO(url, new AirportInterface() {
                @Override
                public void onResponse(List<AirportService> services) {

                    populateRecyclerView(services);
                }
            });


        } catch (Exception e) {
            ErrorReporting.logReport(e);
        }

    }

    @Override
    public void onTouch(int position,View view) {

        AirportService service = services.get(position);

        Intent browserIntent = new Intent(Intent.ACTION_VIEW);
        browserIntent.setData(Uri.parse(service.getLink()));
        startActivity(browserIntent);


    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList(SAConstants.STATE_CHANGED, (ArrayList<? extends Parcelable>) services);


    }
}




