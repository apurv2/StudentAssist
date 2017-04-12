package com.apurv.studentassist.accommodation.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;

import com.apurv.studentassist.R;
import com.apurv.studentassist.accommodation.Interfaces.AccommodationAddsRecyclerInterface;
import com.apurv.studentassist.accommodation.Interfaces.AccommodationBI;
import com.apurv.studentassist.accommodation.Interfaces.OnLoadMoreListener;
import com.apurv.studentassist.accommodation.adapters.AccommodationAddsAdapterLoader;
import com.apurv.studentassist.accommodation.business.rules.AccommodationBO;
import com.apurv.studentassist.accommodation.classes.AccommodationAdd;
import com.apurv.studentassist.accommodation.classes.User;
import com.apurv.studentassist.accommodation.urlInfo.UrlGenerator;
import com.apurv.studentassist.accommodation.urlInfo.UrlInterface;
import com.apurv.studentassist.util.ErrorReporting;
import com.apurv.studentassist.util.L;
import com.apurv.studentassist.util.ObjectSerializer;
import com.apurv.studentassist.util.SAConstants;
import com.apurv.studentassist.util.Utilities;

import java.util.ArrayList;
import java.util.List;

public class UserPostsActivity extends AppCompatActivity implements AccommodationAddsRecyclerInterface {


    private AccommodationAddsAdapterLoader mAccommodationAddsAdapter;
    List<AccommodationAdd> advs = new ArrayList<AccommodationAdd>();
    List<AccommodationAdd> adds = new ArrayList<AccommodationAdd>();
    UrlInterface urlGen = new UrlGenerator();
    String recentUrl = "";


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

        //Navigation Icon
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserPostsActivity.super.onBackPressed();
            }
        });


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

                recentUrl = urlGen.getUserPosts(user.getUserId());


                L.m("url==" + recentUrl);


                new AccommodationBO(recentUrl, new AccommodationBI() {
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


        //  mAccommodationAddsAdapter = new AccommodationAddsAdapter(pageView.getContext(), new ArrayList<AccommodationAdd>(), this);


        mRecyclerVIew = (RecyclerView) findViewById(R.id.recentAdsList);
        mRecyclerVIew.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

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
    public void onTouch(AccommodationAdd add, View view) {


        Intent details = new Intent(this, AdDetailsActivity.class);
        details.putExtra(SAConstants.ACCOMMODATION_ADD_PARCELABLE, (Parcelable) add);
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(this, (View) view, "profile");

        details.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivityForResult(details, 1, options.toBundle());

    }


}
