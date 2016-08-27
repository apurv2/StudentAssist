package com.apurv.studentassist.accommodation.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.apurv.studentassist.R;
import com.apurv.studentassist.accommodation.Interfaces.AccommodationBI;
import com.apurv.studentassist.accommodation.adapters.AccommodationAddsAdapter;
import com.apurv.studentassist.accommodation.business.rules.AccommodationBO;
import com.apurv.studentassist.accommodation.classes.AccommodationAdd;
import com.apurv.studentassist.accommodation.urlInfo.UrlGenerator;
import com.apurv.studentassist.accommodation.urlInfo.UrlInterface;
import com.apurv.studentassist.airport.interfaces.RecyclerTouchInterface;
import com.apurv.studentassist.util.ErrorReporting;
import com.apurv.studentassist.util.L;
import com.apurv.studentassist.util.SAConstants;
import com.apurv.studentassist.util.Utilities;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class RecentlyViewed extends Fragment implements RecyclerTouchInterface {

    View pageView;
    private AccommodationAddsAdapter mAccommodationAddsAdapter;
    List<AccommodationAdd> adds = new ArrayList<AccommodationAdd>();

    private RecyclerView mRecyclerVIew;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        pageView = inflater.inflate(R.layout.activity_recently_viewed,
                container, false);


        return pageView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Utilities.showView(pageView, R.id.recentAdsList);
        Utilities.showView(pageView, R.id.loadingPanel);
        setmRecyclerVIew();
        fetchRecentList();


    }

    private void populateRecyclerView(List<AccommodationAdd> accommodationAdds) {


        Utilities.hideView(pageView, R.id.loadingPanel);

        mAccommodationAddsAdapter.clear();
        mAccommodationAddsAdapter.addAll(accommodationAdds);
        mAccommodationAddsAdapter.notifyDataSetChanged();


    }


    private void fetchRecentList() {

        String url = "";
        try {
            UrlInterface urlGen = new UrlGenerator();
            url = urlGen.getRecentlyViewed();
        } catch (UnsupportedEncodingException e) {
            ErrorReporting.logReport(e);
        }
        new AccommodationBO(url, new AccommodationBI() {
            @Override
            public void onAccommodationAddsReady(ArrayList<AccommodationAdd> advertisements) {


                L.m("populating from remote server");
                adds.clear();
                adds.addAll(advertisements);
                populateRecyclerView(advertisements);

            }

            //not needed
            @Override
            public void onApartmentNamesReady(ArrayList<String> apartmentNames) {

            }


        }, SAConstants.ACCOMMODATION_ADDS);


    }


    public void setmRecyclerVIew() {


        mAccommodationAddsAdapter = new AccommodationAddsAdapter(pageView.getContext(), new ArrayList<AccommodationAdd>(), this);

        mRecyclerVIew = (RecyclerView) pageView.findViewById(R.id.recentAdsList);
        mRecyclerVIew.setAdapter(mAccommodationAddsAdapter);


        mRecyclerVIew.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));


    }

    @Override
    public void onTouch(int position, View view) {

        Intent details = new Intent(getActivity(), AdDetailsActivity.class);
        details.putExtra(SAConstants.ACCOMMODATION_ADD_PARCELABLE, (Parcelable) adds.get(position));
        details.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivityForResult(details, 1);

    }


}
