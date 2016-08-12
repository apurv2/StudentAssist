package com.apurv.studentassist.accommodation.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.apurv.studentassist.R;
import com.apurv.studentassist.accommodation.Interfaces.RecentListInterface;
import com.apurv.studentassist.accommodation.adapters.AccommodationAddsAdapter;
import com.apurv.studentassist.accommodation.business.rules.AccommodationBO;
import com.apurv.studentassist.accommodation.classes.AccommodationAdd;
import com.apurv.studentassist.accommodation.classes.RecentListChecker;
import com.apurv.studentassist.accommodation.urlInfo.UrlGenerator;
import com.apurv.studentassist.accommodation.urlInfo.UrlInterface;
import com.apurv.studentassist.airport.interfaces.RecyclerTouchInterface;
import com.apurv.studentassist.util.ErrorReporting;
import com.apurv.studentassist.util.ObjectSerializer;
import com.apurv.studentassist.util.SAConstants;
import com.apurv.studentassist.util.Utilities;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RecentlyViewed extends Fragment implements RecyclerTouchInterface {

    View pageView;
    private AccommodationAddsAdapter mAccommodationAddsAdapter;
    List<AccommodationAdd> advs = new ArrayList<AccommodationAdd>();
    List<AccommodationAdd> adds = new ArrayList<AccommodationAdd>();

    private RecyclerView mRecyclerVIew;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    UrlInterface urlGen = new UrlGenerator();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        pageView = inflater.inflate(R.layout.activity_recently_viewed,
                container, false);

       // ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Recently Viewed");


        setHasOptionsMenu(true);

        return pageView;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            Utilities.showView(pageView, R.id.recentAdsList);
            Utilities.showView(pageView, R.id.loadingPanel);
            sharedPreferences = getActivity().getSharedPreferences(SAConstants.sharedPreferenceName, 0);
            editor = sharedPreferences.edit();

            setmRecyclerVIew();
            fetchRecentList();

        } catch (Exception e) {
            ErrorReporting.logReport(e);
        }


    }

    private void populateRecyclerView(List<AccommodationAdd> accommodationAdds) {


        editor.putString(SAConstants.RECENTS, Base64.encodeToString(ObjectSerializer.serialize(accommodationAdds), Base64.DEFAULT));
        editor.commit();


        Utilities.hideView(pageView, R.id.loadingPanel);

        mAccommodationAddsAdapter.clear();
        mAccommodationAddsAdapter.addAll(accommodationAdds);
        mAccommodationAddsAdapter.notifyDataSetChanged();

        adds.clear();
        adds.addAll(accommodationAdds);


    }


    private List<AccommodationAdd> fetchRecentList() {


        sharedPreferences = getActivity().getSharedPreferences(SAConstants.sharedPreferenceName, 0);

        final byte[] recentListBytes = Base64.decode(sharedPreferences.getString(SAConstants.RECENTS, ""), Base64.DEFAULT);
        final List<AccommodationAdd> recentsList = (List<AccommodationAdd>) ObjectSerializer.deserialize(recentListBytes);

        if (recentsList == null) {
            return Collections.EMPTY_LIST;
        } else {


            // only addIds
            final List<RecentListChecker> recentListCheckers = new ArrayList<>();
            for (AccommodationAdd add : recentsList) {

                recentListCheckers.add(new RecentListChecker(add.getAddId()));

            }

            String json = new Gson().toJson(recentListCheckers);

            try {

                String url = urlGen.getRecentListCheckerUrl(json);

                new AccommodationBO(url, new RecentListInterface() {
                    @Override
                    public void recentlyVisitedAdvertisements(List<RecentListChecker> recents) {


                        List<AccommodationAdd> finalRecentsList = new ArrayList<>();

                        for (RecentListChecker recentaddId : recents) {

                            for (AccommodationAdd accommodationAdd : recentsList) {

                                if (recentaddId.getAddId().equals(accommodationAdd.getAddId())) {
                                    finalRecentsList.add(accommodationAdd);
                                }

                            }


                        }


                        populateRecyclerView(finalRecentsList);
                    }
                });


            } catch (UnsupportedEncodingException e) {
                ErrorReporting.logReport(e);
            }


        }


        return recentsList;
    }


    public void setmRecyclerVIew() {


        mAccommodationAddsAdapter = new AccommodationAddsAdapter(pageView.getContext(), new ArrayList<AccommodationAdd>(),this);

        mRecyclerVIew = (RecyclerView) pageView.findViewById(R.id.recentAdsList);
        mRecyclerVIew.setAdapter(mAccommodationAddsAdapter);


        mRecyclerVIew.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));


    }

    @Override
    public void onTouch(int position,View view) {

        Intent details = new Intent(getActivity(), AdDetailsActivity.class);
        details.putExtra(SAConstants.ACCOMMODATION_ADD_PARCELABLE, (Parcelable) adds.get(position));
        details.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivityForResult(details, 1);

    }






}
