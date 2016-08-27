package com.apurv.studentassist.accommodation.activities;

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.apurv.studentassist.R;
import com.apurv.studentassist.accommodation.Dialogs.AlertDialogL;
import com.apurv.studentassist.accommodation.Interfaces.AccommodationActivityToFragmentInterface;
import com.apurv.studentassist.accommodation.Interfaces.AccommodationBI;
import com.apurv.studentassist.accommodation.business.rules.AccommodationBO;
import com.apurv.studentassist.accommodation.classes.AccommodationAdd;
import com.apurv.studentassist.accommodation.fragments.AccommodationDrawerFragment;
import com.apurv.studentassist.accommodation.fragments.AdvancedSearchFragment;
import com.apurv.studentassist.accommodation.fragments.SearchAccomodationFragment;
import com.apurv.studentassist.accommodation.urlInfo.UrlGenerator;
import com.apurv.studentassist.accommodation.urlInfo.UrlInterface;
import com.apurv.studentassist.util.Alerts;
import com.apurv.studentassist.util.SAConstants;
import com.apurv.studentassist.util.interfaces.EmptyInterface;
import com.apurv.studentassist.util.interfaces.LodingDialogInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;

//import com.squareup.leakcanary.LeakCanary;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class AccommodationActivity extends AppCompatActivity implements
        MaterialTabListener, LodingDialogInterface, EmptyInterface {


    private MaterialTabHost mTabs;
    private ViewPager mViewPager;
    public Map<String, String[]> spinnerInformation;

    // Need this to link with the Snackbar
    private CoordinatorLayout mCoordinator;
    //Need this to set the title of the app bar
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private AppBarLayout appBarLayout;
    public Toolbar toolbar;
    String[] aptNamesArray;
    boolean reEntryFlag = false;
    Bundle bundle;

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_accomodation);

        //  LeakCanary.install((Application) StudentAssistApplication.getAppContext());

        if (savedInstanceState != null) {
            bundle = savedInstanceState;
            reEntryFlag = true;
        }


        mCoordinator = (CoordinatorLayout) findViewById(R.id.root_coordinator);
        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();


        toolbar = (Toolbar) findViewById(R.id.accommodationToolbar);
        setSupportActionBar(toolbar);

        AccommodationDrawerFragment accommodationDrawerFragment = (AccommodationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigationDrawer);

        accommodationDrawerFragment.setUp(R.id.navigationDrawer, (DrawerLayout) findViewById(R.id.drawerLayoutId), toolbar);


        spinnerInformation = new HashMap<String, String[]>();
        spinnerInformation.put(SAConstants.APARTMENT_TYPE_SPINNER_INFORMATION, SAConstants.APARTMENT_TYPES);
        spinnerInformation.put(SAConstants.GENDER_SPINNER_INFORMATION, SAConstants.MALE_FEMALE);


        mTabs = (MaterialTabHost) findViewById(R.id.tabs);
        mViewPager = (ViewPager) findViewById(R.id.pager);


        mViewPager.setAdapter(new MyViewPagerAdapter(getSupportFragmentManager()));
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {


            @Override
            public void onPageSelected(int position) {

                if (toolbar != null) {
                    toolbar.setTitle(SAConstants.pageTitles[position].toString());
                    mTabs.setSelectedNavigationItem(position);
                }

            }
        });


        MyViewPagerAdapter adapter = (MyViewPagerAdapter) mViewPager.getAdapter();

        for (int i = 0; i < adapter.getCount(); i++) {

            mTabs.addTab(mTabs.newTab().setIcon(adapter.getIcon(i)).setTabListener(this));


        }

        View v = findViewById(R.id.fab);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityOptions options =
                        ActivityOptions.makeSceneTransitionAnimation(AccommodationActivity.this, (View) v, v.getTransitionName());

                Intent details = new Intent(getApplicationContext(), PostAccomodationActivity.class);
                details.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivityForResult(details, 1, options.toBundle());
            }
        });

    }// end of on create


    public void getApartmentNames(final AccommodationActivityToFragmentInterface aInterface) {
        UrlInterface urlInterface = new UrlGenerator();

        if (reEntryFlag && (bundle.getStringArray(SAConstants.APARTMENT_NAME) != null)) {

            aptNamesArray = bundle.getStringArray(SAConstants.APARTMENT_NAME);
            spinnerInformation.put(SAConstants.APARTMENT_NAME_SPINNER_INFORMATION, aptNamesArray);
            aInterface.onApartmentNamesListReady(aptNamesArray);

        } else {

            String url = urlInterface.getApartmentNamesUrl(SAConstants.ALL);

            new AccommodationBO(url, new AccommodationBI() {

                //not needed
                @Override
                public void onAccommodationAddsReady(ArrayList<AccommodationAdd> advertisements) {


                }

                @Override
                public void onApartmentNamesReady(ArrayList<String> apartmentNames) {

                    if (apartmentNames != null)

                        aptNamesArray = (String[]) apartmentNames.toArray(new String[apartmentNames.size()]);
                    spinnerInformation.put(SAConstants.APARTMENT_NAME_SPINNER_INFORMATION, aptNamesArray);
                    aInterface.onApartmentNamesListReady(aptNamesArray);

                }


            }, SAConstants.APARTMENT_NAMES);
        }


    }


    class MyViewPagerAdapter extends FragmentStatePagerAdapter {

        int tabImageArray[] = {R.drawable.ic_simple_search, R.drawable.ic_advanced_search, R.drawable.ic_recents};
        String[] tabStringArray = SAConstants.pageTitles;


        public MyViewPagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);


        }

        @Override
        public CharSequence getPageTitle(int position) {


            return SAConstants.pageTitles[position].toString();


        }


        @Override
        public Fragment getItem(int position) {

            if (position == 0) {

                return new SearchAccomodationFragment();


            } else {
                if (position == 1) {

                    return new AdvancedSearchFragment();


                } else {
                    return new RecentlyViewed();
                }


            }

        }

        private Drawable getIcon(int position) {
            return getResources().getDrawable(tabImageArray[position]);
        }


        @Override
        public int getCount() {
            return 3;
        }
    }


    public void onTabSelected(MaterialTab materialTab) {

        mViewPager.setCurrentItem(materialTab.getPosition());


    }

    @Override
    public void onTabReselected(MaterialTab materialTab) {

    }

    @Override
    public void onTabUnselected(MaterialTab materialTab) {

    }

    @Override
    public void onResume() {  // After a pause OR at startup
        super.onResume();
        //Refresh your stuff here
    }


    //Display dialog response about notification subscription status
    @Override
    public void onResponse(String response) {

        String alertText = "";

        if (response.equals(SAConstants.SUCCESS)) {
            alertText = Alerts.alerts.get(0);
        } else if (response.equals(SAConstants.ALREADY_SUBSCRIBED)) {
            alertText = Alerts.alerts.get(1062);
        } else {
            alertText = Alerts.errors.get(6);
        }


        Bundle bundle = new Bundle();
        bundle.putString(SAConstants.ALERT_TEXT, alertText);

        AlertDialogL deleteOption = new AlertDialogL();
        deleteOption.setArguments(bundle);
        deleteOption.show(getSupportFragmentManager(), "");


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putStringArray(SAConstants.APARTMENT_NAME, aptNamesArray);


    }
}
