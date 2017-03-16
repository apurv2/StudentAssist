package com.apurv.studentassist.accommodation.Dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.android.volley.Request;
import com.apurv.studentassist.R;
import com.apurv.studentassist.accommodation.adapters.UniversitiesListAdapter;
import com.apurv.studentassist.accommodation.classes.University;
import com.apurv.studentassist.accommodation.urlInfo.UrlGenerator;
import com.apurv.studentassist.accommodation.urlInfo.UrlInterface;
import com.apurv.studentassist.internet.NetworkInterface;
import com.apurv.studentassist.internet.StudentAssistBO;
import com.apurv.studentassist.util.ErrorReporting;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by akamalapuri on 3/15/2017.
 */

public class UniversitiesListDialog extends DialogFragment {

    private RecyclerView mRecyclerVIew;
    private View pageView;
    UniversitiesListAdapter universitiesListAdapter;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the Builder class for convenient dialog construction
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        pageView = inflater.inflate(R.layout.fragment_university_list, null);
        builder.setView(pageView);

        setmRecyclerVIew();

        geFromServer();

        return super.onCreateDialog(savedInstanceState);
    }

    private void geFromServer() {


        StudentAssistBO studentAssistBo = new StudentAssistBO();
        UrlInterface urlgen = new UrlGenerator();

        studentAssistBo.volleyRequest(urlgen.getUniversitieListUrl(), new NetworkInterface() {
            @Override
            public void onResponseUpdate(String jsonResponse) {

                Gson gson = new Gson();
                List<University> universitiesList = gson.fromJson(jsonResponse, new TypeToken<List<University>>() {
                }.getType());

                populateRecyclerView(universitiesList);
            }


        }, null, Request.Method.GET);

    }

    private void populateRecyclerView(List<University> universitiesList) {

        try {

            universitiesListAdapter.clear();
            universitiesListAdapter.addAll(universitiesList);


        } catch (Exception e) {
            ErrorReporting.logReport(e);
        }
    }


    public void setmRecyclerVIew() {

        try {
            mRecyclerVIew = (RecyclerView) pageView.findViewById(R.id.universitiesList);
            mRecyclerVIew.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));


            universitiesListAdapter = new UniversitiesListAdapter(pageView.getContext(), new ArrayList<University>());
            mRecyclerVIew.setAdapter(universitiesListAdapter);

        } catch (Exception e) {
            ErrorReporting.logReport(e);
        }

    }


}
