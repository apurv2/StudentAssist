package com.apurv.studentassist.accommodation.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.apurv.studentassist.R;

import butterknife.ButterKnife;

public class MapsFragment extends Fragment {


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        //Setting up the parent view using Layout Inflation
       View pageView = inflater.inflate(R.layout.activity_search_accomodation, container, false);
        ButterKnife.bind(this, pageView);


        return pageView;
    }

}



