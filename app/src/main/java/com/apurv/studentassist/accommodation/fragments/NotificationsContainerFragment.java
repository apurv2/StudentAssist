package com.apurv.studentassist.accommodation.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.apurv.studentassist.R;
import com.apurv.studentassist.accommodation.Interfaces.AccommodationBI;
import com.apurv.studentassist.accommodation.Interfaces.OnLoadMoreListener;
import com.apurv.studentassist.accommodation.activities.AdDetailsActivity;
import com.apurv.studentassist.accommodation.adapters.AccommodationAddsAdapterLoader;
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

public class NotificationsContainerFragment extends Fragment implements RecyclerTouchInterface {

    View pageView;
    private AccommodationAddsAdapterLoader mAccommodationAddsAdapter;
    List<AccommodationAdd> adds = new ArrayList<AccommodationAdd>();

    private RecyclerView mRecyclerVIew;
    String recentUrl = "";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        pageView = inflater.inflate(R.layout.activity_recently_viewed,
                container, false);


        setmRecyclerVIew();
        fetchRecentList();
        return pageView;
    }

    private void populateRecyclerView(List<AccommodationAdd> accommodationAdds) {


        Utilities.hideView(pageView, R.id.loadingPanel);

        mAccommodationAddsAdapter.clear();
        mAccommodationAddsAdapter.addAll(accommodationAdds);
        mAccommodationAddsAdapter.notifyDataSetChanged();


    }


    private void fetchRecentList() {

        try {
            UrlInterface urlGen = new UrlGenerator();
            recentUrl = urlGen.getUserNotificationsUrl();
        } catch (UnsupportedEncodingException e) {
            ErrorReporting.logReport(e);
        }
        new AccommodationBO(recentUrl, new AccommodationBI() {
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


        mRecyclerVIew = (RecyclerView) pageView.findViewById(R.id.recentAdsList);
        mRecyclerVIew.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));

        mAccommodationAddsAdapter = new AccommodationAddsAdapterLoader(pageView.getContext(), new ArrayList<AccommodationAdd>(), this, mRecyclerVIew);
        mRecyclerVIew.setAdapter(mAccommodationAddsAdapter);

        mAccommodationAddsAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(int position) {

                mAccommodationAddsAdapter.add(null);


                new AccommodationBO(UrlGenerator.getPaginationUrl(recentUrl, position), new AccommodationBI() {
                    @Override
                    public void onAccommodationAddsReady(ArrayList<AccommodationAdd> advertisements) {
                        mAccommodationAddsAdapter.pop();

                        L.m("populating pagination");
                        adds.addAll(advertisements);
                        addToRecyclerView(advertisements);

                        //   remove progress item
                        mAccommodationAddsAdapter.setLoaded();
                    }

                    //not needed
                    @Override
                    public void onApartmentNamesReady(ArrayList<String> apartmentNames) {

                    }


                }, SAConstants.ACCOMMODATION_ADDS);


            }
        });

    }

    private void addToRecyclerView(ArrayList<AccommodationAdd> advertisements) {

        try {

            mAccommodationAddsAdapter.addAll(advertisements);
        } catch (Exception e) {
            ErrorReporting.logReport(e);
        }
    }

    @Override
    public void onTouch(int position, View view) {

        Intent details = new Intent(getActivity(), AdDetailsActivity.class);
        details.putExtra(SAConstants.ACCOMMODATION_ADD_PARCELABLE, (Parcelable) adds.get(position));
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(getActivity(), (View) view, "profile");

        details.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivityForResult(details, 1, options.toBundle());
    }


}
