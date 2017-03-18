package com.apurv.studentassist.accommodation.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

import static com.apurv.studentassist.R.id.universitiesList;
import static com.apurv.studentassist.util.Utilities.slideDown;
import static com.apurv.studentassist.util.Utilities.slideUp;

/**
 * Created by akamalapuri on 3/15/2017.
 */


public class UniversitiesListActivity extends AppCompatActivity implements RecyclerTouchInterface {

    private RecyclerView mRecyclerVIew;
    UniversitiesListAdapter universitiesListAdapter;
    private List<University> universityList = new ArrayList<>();
    ArrayList selectedUniversityIds = new ArrayList<>();
    ArrayList selectedUniversityNames = new ArrayList<>();
    List displayUnivList = new ArrayList<>();


    @Bind(R.id.sendUniversities)
    FloatingActionButton sendUniversities;

    @Bind(R.id.searchForUniv)
    EditText searchForUnivTextView;


    @Bind(R.id.bottomBar)
    Button bottomBar;

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

        getFromServer();
       /* List abc = new ArrayList<University>();


        abc.add(new University(1, "UT Arlington", "description",
                Arrays.asList("https://thumbs.dreamstime.com/x/san-diego-state-university-bell-tower-6140195.jpg "), 3, "Arlington, Texas", 1968, 30));

        abc.add(new University(3, "SUNY Buffalo", "description",
                Arrays.asList(
                        "https://thumbs.dreamstime.com/x/san-diego-state-university-bell-tower-6140195.jpg "),
                2, "Buffalo, New York", 1929, 30));

        universityList.addAll(abc);
        populateRecyclerView(abc);
*/
        //searchForUnivTextView.setOnQueryTextListener(this);

    }

    @OnClick(R.id.sendUniversities)
    void submitButton(View view) {

        Intent homeScreenIntent = new Intent(this, HomeScreenActivity.class);

        homeScreenIntent.putParcelableArrayListExtra(SAConstants.UNIVERSITY_IDS, selectedUniversityIds);


        homeScreenIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(homeScreenIntent);


    }

    @OnTextChanged(value = R.id.searchForUniv,
            callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void afterEmailInput(CharSequence text) {
        try {
            displayUnivList.clear();
            String queryText = text.toString();

            if (queryText.equals("")) {

                populateRecyclerView(universityList);

            } else {

                for (University univ : universityList) {
                    if (univ.getUniversityName().matches("(?i).*" + queryText + ".*")) {
                        displayUnivList.add(univ);
                    }
                }
                populateRecyclerView(displayUnivList);
           }
        } catch (Exception e) {
            ErrorReporting.logReport(e);
        }

    }

    private void getFromServer() {


        StudentAssistBO studentAssistBo = new StudentAssistBO();
        UrlInterface urlgen = new UrlGenerator();

        studentAssistBo.volleyRequest(urlgen.getUniversitieListUrl(), new NetworkInterface() {
            @Override
            public void onResponseUpdate(String jsonResponse) {

                try {
                    Gson gson = new Gson();
                    List<University> universitiesList = gson.fromJson(jsonResponse, new TypeToken<List<University>>() {
                    }.getType());

                    universityList = universitiesList;
                    populateRecyclerView(universitiesList);
                } catch (Exception e) {
                    ErrorReporting.logReport(e);
                }
            }


        }, null, Request.Method.GET);

    }

    private void populateRecyclerView(List<University> universitiesList) {

        try {

            //universitiesListAdapter.clear();
            universitiesListAdapter.updateList(universitiesList);


        } catch (Exception e) {
            ErrorReporting.logReport(e);
        }
    }


    public void setmRecyclerVIew() {

        try {
            mRecyclerVIew = (RecyclerView) findViewById(universitiesList);
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
     * @param clickedUniversityId
     * @param view
     */
    @Override
    public void onTouch(int clickedUniversityId, View view) {

        //Code to get clicked Position in Recycler view and get the actual position in the original
        //list to send to server.
        int clickedPosition = 0;
        for (University univ : universityList) {
            if (univ.getUniversityId() == clickedUniversityId) {
                break;
            }
            clickedPosition++;
        }


        if (selectedUniversityIds.contains(universityList.get(clickedPosition).getUniversityId())) {
            selectedUniversityIds.remove(new Integer(universityList.get(clickedPosition).getUniversityId()));
            selectedUniversityNames.remove(universityList.get(clickedPosition).getUniversityName());
        } else {
            selectedUniversityIds.add(universityList.get(clickedPosition).getUniversityId());
            selectedUniversityNames.add(universityList.get(clickedPosition).getUniversityName());
        }

        String selectedUnivsText = android.text.TextUtils.join(",", selectedUniversityNames);
        bottomBar.setText(selectedUnivsText);


        if (selectedUniversityIds.isEmpty()) {
            Utilities.hideViewUsingAnimation(bottomBar, slideDown);
            Utilities.revealHide(sendUniversities);
        } else {
            Utilities.showViewUsingAnimation(bottomBar, slideUp);
            Utilities.revealShow(sendUniversities);
        }
    }

}
