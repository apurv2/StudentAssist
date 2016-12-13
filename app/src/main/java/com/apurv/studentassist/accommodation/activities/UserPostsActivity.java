package com.apurv.studentassist.accommodation.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;

import com.apurv.studentassist.R;
import com.apurv.studentassist.accommodation.Interfaces.AccommodationBI;
import com.apurv.studentassist.accommodation.adapters.AccommodationAddsAdapter;
import com.apurv.studentassist.accommodation.business.rules.AccommodationBO;
import com.apurv.studentassist.accommodation.classes.AccommodationAdd;
import com.apurv.studentassist.accommodation.classes.User;
import com.apurv.studentassist.accommodation.urlInfo.UrlGenerator;
import com.apurv.studentassist.accommodation.urlInfo.UrlInterface;
import com.apurv.studentassist.airport.interfaces.RecyclerTouchInterface;
import com.apurv.studentassist.util.ErrorReporting;
import com.apurv.studentassist.util.L;
import com.apurv.studentassist.util.ObjectSerializer;
import com.apurv.studentassist.util.SAConstants;
import com.apurv.studentassist.util.Utilities;

import java.util.ArrayList;
import java.util.List;

public class UserPostsActivity extends AppCompatActivity implements RecyclerTouchInterface {


    private AccommodationAddsAdapter mAccommodationAddsAdapter;
    List<AccommodationAdd> advs = new ArrayList<AccommodationAdd>();
    List<AccommodationAdd> adds = new ArrayList<AccommodationAdd>();
    UrlInterface urlGen = new UrlGenerator();


    private RecyclerView mRecyclerVIew;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    View pageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_posts);


        Toolbar toolbar;
        toolbar = (Toolbar) findViewById(R.id.applicationBar);
        toolbar.setTitle(SAConstants.USER_POSTS);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        pageView = findViewById(R.id.rootLayoutYourPosts);
        try {

            Utilities.showView(findViewById(R.id.recentAdsList));
            Utilities.showView(findViewById(R.id.loadingPanel));

            sharedPreferences = getSharedPreferences(SAConstants.SHARED_PREFERENCE_NAME, 0);
            editor = sharedPreferences.edit();

            setmRecyclerVIew();
            fetchUserPosts();

        } catch (Exception e) {
            ErrorReporting.logReport(e);
        }

    }

    private void populateRecyclerView() {

        Utilities.hideView(findViewById(R.id.loadingPanel));


        mAccommodationAddsAdapter.clear();
        mAccommodationAddsAdapter.addAll(advs);
        mAccommodationAddsAdapter.notifyDataSetChanged();

        adds.clear();
        adds.addAll(advs);


    }

    private void fetchUserPosts() {
        try {


            sharedPreferences = getSharedPreferences(SAConstants.SHARED_PREFERENCE_NAME, 0);

            byte[] userBytes = Base64.decode(sharedPreferences.getString(SAConstants.USER, ""), Base64.DEFAULT);
            User user = (User) ObjectSerializer.deserialize(userBytes);

            if (user != null) {

                String url = urlGen.getUserPosts(user.getUserId());

                L.m("url==" + url);


                new AccommodationBO(url, new AccommodationBI() {
                    @Override
                    public void onAccommodationAddsReady(ArrayList<AccommodationAdd> advertisements) {

                        advs.clear();
                        advs.addAll(advertisements);

                        populateRecyclerView();


                    }

                    @Override
                    public void onApartmentNamesReady(ArrayList<String> apartmentNames) {

                    }


                }, SAConstants.ACCOMMODATION_ADDS);
            }


        } catch (Exception e) {
            ErrorReporting.logReport(e);
        }

    }

    public void setmRecyclerVIew() {


        mAccommodationAddsAdapter = new AccommodationAddsAdapter(pageView.getContext(), new ArrayList<AccommodationAdd>(), this);

        mRecyclerVIew = (RecyclerView) pageView.findViewById(R.id.recentAdsList);
        mRecyclerVIew.setAdapter(mAccommodationAddsAdapter);


        mRecyclerVIew.setLayoutManager(new LinearLayoutManager(getApplicationContext()));


    }

    @Override
    public void onTouch(int position, View view) {

        Intent details = new Intent(getApplicationContext(), AdDetailsActivity.class);
        details.putExtra(SAConstants.ACCOMMODATION_ADD_PARCELABLE, (Parcelable) adds.get(position));
        details.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivityForResult(details, 1);

    }


}
