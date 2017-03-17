package com.apurv.studentassist.accommodation.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.android.volley.Request;
import com.apurv.studentassist.R;
import com.apurv.studentassist.accommodation.adapters.UniversitiesListAdapter;
import com.apurv.studentassist.accommodation.classes.University;
import com.apurv.studentassist.accommodation.urlInfo.UrlGenerator;
import com.apurv.studentassist.accommodation.urlInfo.UrlInterface;
import com.apurv.studentassist.airport.interfaces.RecyclerTouchInterface;
import com.apurv.studentassist.internet.NetworkInterface;
import com.apurv.studentassist.internet.StudentAssistBO;
import com.apurv.studentassist.util.ErrorReporting;
import com.apurv.studentassist.util.SAConstants;
import com.apurv.studentassist.util.Utilities;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.apurv.studentassist.util.Utilities.fadeIn;
import static com.apurv.studentassist.util.Utilities.fadeOut;

/**
 * Created by akamalapuri on 3/15/2017.
 */


public class UniversitiesListActivity extends AppCompatActivity implements RecyclerTouchInterface {

    private RecyclerView mRecyclerVIew;
    UniversitiesListAdapter universitiesListAdapter;
    private List<University> universityList;
    ArrayList selectedUniversityIds = new ArrayList<>();

    @Bind(R.id.sendUniversities)
    FloatingActionButton sendUniversities;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_university_list);
        ButterKnife.bind(this);


        // setup toolbar
        Toolbar toolbar;
        toolbar = (Toolbar) findViewById(R.id.applicationBar);
        toolbar.setTitle(SAConstants.SELECT_UNIVERSITIES);
        setSupportActionBar(toolbar);
        setmRecyclerVIew();

        //  geFromServer();
        List abc = new ArrayList<University>();


        abc.add(new University(1, "UT Arlington", "description",
                Arrays.asList("https://thumbs.dreamstime.com/x/san-diego-state-university-bell-tower-6140195.jpg "), 3, "Arlington, Texas", 1968, 30));

        abc.add(new University(3, "SUNY Buffalo", "description",
                Arrays.asList(
                        "https://thumbs.dreamstime.com/x/san-diego-state-university-bell-tower-6140195.jpg "),
                2, "Buffalo, New York", 1929, 30));

        populateRecyclerView(abc);


    }

    @OnClick(R.id.sendUniversities)
    void submitButton(View view) {

        //selectedUniversityIds

        Intent homeScreenIntent = new Intent(this, HomeScreenActivity.class);

        homeScreenIntent.putParcelableArrayListExtra(SAConstants.UNIVERSITY_IDS, selectedUniversityIds);


        homeScreenIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(homeScreenIntent);


    }

    private void geFromServer() {


        StudentAssistBO studentAssistBo = new StudentAssistBO();
        UrlInterface urlgen = new UrlGenerator();

        studentAssistBo.volleyRequest(urlgen.getUniversitieListUrl(), new NetworkInterface() {
            @Override
            public void onResponseUpdate(String jsonResponse) {

                try {
                    Gson gson = new Gson();
                    List<University> universitiesList = gson.fromJson(jsonResponse, new TypeToken<List<University>>() {
                    }.getType());

                    populateRecyclerView(universitiesList);
                } catch (Exception e) {
                    ErrorReporting.logReport(e);
                }
            }


        }, null, Request.Method.GET);

    }

    private void populateRecyclerView(List<University> universitiesList) {

        try {

            universitiesListAdapter.clear();
            universitiesListAdapter.addAll(universitiesList);
            universityList = universitiesList;


        } catch (Exception e) {
            ErrorReporting.logReport(e);
        }
    }


    public void setmRecyclerVIew() {

        try {
            mRecyclerVIew = (RecyclerView) findViewById(R.id.universitiesList);
            mRecyclerVIew.setLayoutManager(new LinearLayoutManager(getApplicationContext()));


            universitiesListAdapter = new UniversitiesListAdapter(this, new ArrayList<University>(), this, sendUniversities);
            mRecyclerVIew.setAdapter(universitiesListAdapter);

        } catch (Exception e) {
            ErrorReporting.logReport(e);
        }

    }

    /**
     * RecyclerView on touch event
     *
     * @param position
     * @param view
     */
    @Override
    public void onTouch(int position, View view) {

        if (selectedUniversityIds.contains(universityList.get(position).getUniversityId())) {
            selectedUniversityIds.remove(new Integer(universityList.get(position).getUniversityId()));
        } else {
            selectedUniversityIds.add(universityList.get(position).getUniversityId());
        }

        if (selectedUniversityIds.isEmpty()) {
            Utilities.fadeOutView(sendUniversities, fadeOut);
        } else {
            Utilities.fadeInView(sendUniversities, fadeIn);
        }


    }
}
