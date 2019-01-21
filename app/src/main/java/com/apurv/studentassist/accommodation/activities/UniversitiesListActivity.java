package com.apurv.studentassist.accommodation.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.apurv.studentassist.R;
import com.apurv.studentassist.accommodation.Interfaces.UniversitiesRecyclerInterface;
import com.apurv.studentassist.accommodation.adapters.UniversitiesListAdapter;
import com.apurv.studentassist.accommodation.classes.University;
import com.apurv.studentassist.accommodation.urlInfo.UrlGenerator;
import com.apurv.studentassist.accommodation.urlInfo.UrlInterface;
import com.apurv.studentassist.internet.NetworkInterface;
import com.apurv.studentassist.internet.StudentAssistBO;
import com.apurv.studentassist.util.ErrorReporting;
import com.apurv.studentassist.util.SAConstants;
import com.apurv.studentassist.util.Utilities;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

import static com.apurv.studentassist.R.id.universitiesList;
import static com.apurv.studentassist.util.Utilities.slideDown;
import static com.apurv.studentassist.util.Utilities.slideUp;

/**
 * Created by akamalapuri on 3/15/2017.
 */


public class UniversitiesListActivity extends AppCompatActivity implements UniversitiesRecyclerInterface {


    UniversitiesListAdapter universitiesListAdapter;
    private List<University> universityList = new ArrayList<>();
    ArrayList selectedUniversityIds = new ArrayList<>();
    ArrayList selectedUniversityNames = new ArrayList<>();
    List displayUnivList = new ArrayList<>();


    @BindView(R.id.sendUniversities)
    FloatingActionButton sendUniversities;

    @BindView(R.id.searchForUniv)
    EditText searchForUnivTextView;


    @BindView(R.id.bottomBar)
    Button bottomBar;

    @BindView(R.id.universitiesList)
    RecyclerView mRecyclerVIew;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_university_list);
        ButterKnife.bind(this);

        ViewGroup.MarginLayoutParams marginLayoutParams =
                (ViewGroup.MarginLayoutParams) mRecyclerVIew.getLayoutParams();
        marginLayoutParams.setMargins(0, 20, 0, 20);
        mRecyclerVIew.setLayoutParams(marginLayoutParams);


        // setup toolbar
        Toolbar toolbar;
        toolbar = (Toolbar) findViewById(R.id.applicationBar);
        toolbar.setTitle(SAConstants.SELECT_UNIVERSITIES);
        setSupportActionBar(toolbar);
        setmRecyclerVIew();

        Intent intent = this.getIntent();
        getFromServer(intent.getExtras().getBoolean(SAConstants.GET_UNIVERSITY_NAMES_WITH_USERS_LIST));
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

    private void prePopulateUserUniversities(List<University> universitiesList) {


        for (University university : universitiesList) {
            if (university.isSelected()) {

                selectedUniversityIds.add(university.getUniversityId());
                selectedUniversityNames.add(university.getUniversityName());
            }
        }
        String selectedUnivsText = android.text.TextUtils.join(",", selectedUniversityNames);
        bottomBar.setText(selectedUnivsText);


        if (!selectedUniversityIds.isEmpty()) {

            Utilities.showViewUsingAnimation(bottomBar, slideUp);
            Utilities.revealShow(sendUniversities);
        }

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
                    } else if (univ.getUnivAcronym().matches("(?i).*" + queryText + ".*")) {
                        displayUnivList.add(univ);

                    }
                }
                populateRecyclerView(displayUnivList);
            }
        } catch (Exception e) {
            ErrorReporting.logReport(e);
        }

    }

    private void getFromServer(boolean userPreferences) {


        StudentAssistBO studentAssistBo = new StudentAssistBO();
        UrlInterface urlgen = new UrlGenerator();

        String url;

        if (userPreferences) {
            url = urlgen.getAllUnivsInclUserSelectd();
        } else {

            url = urlgen.getUniversitieListUrl();
        }


        studentAssistBo.volleyRequest(url, new NetworkInterface() {
            @Override
            public void onResponseUpdate(String jsonResponse) {

                try {
                    Gson gson = new Gson();
                    List<University> universitiesList = gson.fromJson(jsonResponse, new TypeToken<List<University>>() {
                    }.getType());

                    universityList = universitiesList;

                    prePopulateUserUniversities(universitiesList);
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
     * @param
     * @param view
     */
    @Override
    public boolean onTouch(University university, View view) {

        if (selectedUniversityIds.contains(university.getUniversityId())) {
            selectedUniversityIds.remove(new Integer(university.getUniversityId()));
            selectedUniversityNames.remove(university.getUniversityName());
        } else {

            if (selectedUniversityIds.size() >= 4) {
                Toast.makeText(this, SAConstants.UNVS_LIMIT_EXCEEDED, Toast.LENGTH_LONG).show();
                return false;
            }

            selectedUniversityIds.add(university.getUniversityId());
            selectedUniversityNames.add(university.getUniversityName());

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

        return true;
    }

}
