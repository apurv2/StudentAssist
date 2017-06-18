package com.apurv.studentassist.accommodation.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.android.volley.Request;
import com.apurv.studentassist.R;
import com.apurv.studentassist.accommodation.Interfaces.AccommodationAddsRecyclerInterface;
import com.apurv.studentassist.accommodation.Interfaces.AccommodationBI;
import com.apurv.studentassist.accommodation.Interfaces.DialogCallback;
import com.apurv.studentassist.accommodation.Interfaces.OnLoadMoreListener;
import com.apurv.studentassist.accommodation.activities.AdDetailsActivity;
import com.apurv.studentassist.accommodation.activities.NotificationSettingsActivity;
import com.apurv.studentassist.accommodation.adapters.AccommodationAddsAdapterLoader;
import com.apurv.studentassist.accommodation.business.rules.AccommodationBO;
import com.apurv.studentassist.accommodation.classes.AccommodationAdd;
import com.apurv.studentassist.accommodation.classes.ApartmentNamesInUnivs;
import com.apurv.studentassist.accommodation.classes.ApartmentNamesWithType;
import com.apurv.studentassist.accommodation.urlInfo.UrlGenerator;
import com.apurv.studentassist.accommodation.urlInfo.UrlInterface;
import com.apurv.studentassist.internet.NetworkInterface;
import com.apurv.studentassist.internet.StudentAssistBO;
import com.apurv.studentassist.util.ErrorReporting;
import com.apurv.studentassist.util.L;
import com.apurv.studentassist.util.SAConstants;
import com.apurv.studentassist.util.Utilities;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class AdvancedSearchFragment extends Fragment implements
        OnItemSelectedListener, DialogCallback, AccommodationAddsRecyclerInterface {


    private View pageView;
    Spinner aptTypes, aptNames, sex, universityNames;
    UrlInterface urlGen = new UrlGenerator();
    private RecyclerView mRecyclerVIew;
    private AccommodationAddsAdapterLoader mAccommodationAddsAdapter;
    ArrayList<AccommodationAdd> adds = new ArrayList<AccommodationAdd>();
    String aptNamesVal = "", sexVal = "", univVal = "";
    ArrayList<String> mApartmentNames;
    String recentUrl = "";
    List<String> universityIdsList = new ArrayList<String>();
    ArrayAdapter<String> universityNamesAdapter;
    ArrayList<ApartmentNamesInUnivs> apartmentNamesInUnivs;
    ArrayAdapter<String> aptNamesAdapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        //Setting up the parent view using Layout Inflation
        pageView = inflater.inflate(R.layout.activity_advanced_search, container, false);

        // hide loader
        Utilities.hideView(pageView, R.id.loader);


        // Initialize and set Spinners
        setSpinners();

        //   populateApartmentSpinner();
        handleSearchButtonClick();

        setmRecyclerVIew();
        setHasOptionsMenu(true);


        if (savedInstanceState != null) {

            populateRecyclerView(savedInstanceState.getParcelableArrayList(SAConstants.STATE_CHANGED));
            apartmentNamesInUnivs = savedInstanceState.getParcelableArrayList(SAConstants.APARTMENT_NAMES);

            universityNamesAdapter.remove("");
            for (ApartmentNamesInUnivs university : apartmentNamesInUnivs) {
                universityNamesAdapter.add(university.getUniversityName());
            }
            universityNames.setSelection(savedInstanceState.getInt(SAConstants.UNIVERSITY_NAME_POSITION));

            addApartmentNames();


            aptNames.setSelection(savedInstanceState.getInt(SAConstants.APARTMENT_NAME_POSITION));
            aptTypes.setSelection(savedInstanceState.getInt(SAConstants.APARTMENT_TYPE_POSITION));
            sex.setSelection(savedInstanceState.getInt(SAConstants.GENDER_POSITION));

        } else {

            getApartmentNames();
        }

        return pageView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.subscribe, menu);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.ic_subscribe:
                Intent subscriptionIntent = new Intent(getActivity(), NotificationSettingsActivity.class);
                startActivity(subscriptionIntent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void OnDialogResponse(int response) throws UnsupportedEncodingException {
        insertNotifications();
    }


    private void insertNotifications() {

       /* final LoadingDialog loadingDialog = Utilities.showLoadingDialog(SAConstants.REQUESTING_SUBSCTIPTION, getActivity().getSupportFragmentManager());

        SharedPreferences sharedPreferences = getActivity().getApplicationContext().getSharedPreferences(SAConstants.SHARED_PREFERENCE_NAME, 0);
        byte[] userInformationBytes = Base64.decode(sharedPreferences.getString(SAConstants.USER, ""), Base64.DEFAULT);
        User user = (User) ObjectSerializer.deserialize(userInformationBytes);
        String gcmId = sharedPreferences.getString(SAConstants.GCM_ID, "");


        try {

            TelephonyManager telephonyManager = (TelephonyManager) getActivity().getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
            String deviceId = telephonyManager.getDeviceId();

            String url = urlGen.getSubscribeNotificationsUrl("1", aptNames.getSelectedItem().toString(), sex.getSelectedItem().toString(), user.getUserId(), gcmId, deviceId);

            new NotificationBO(loadingDialog, url);


        } catch (Exception e) {
            ErrorReporting.logReport(e);
        }*/


    }

    private void handleSearchButtonClick() {
        try {


            Button searchButton = (Button) pageView.findViewById(R.id.searchAd);
            searchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {


                        String apartmentName = aptNames.getSelectedItem().toString();
                        String gender = sex.getSelectedItem().toString();
                        String universityVal = universityNames.getSelectedItem().toString();
                        int universityId = -1;
                        for (ApartmentNamesInUnivs univ : apartmentNamesInUnivs) {
                            if (univ.getUniversityName().equals(universityVal)) {
                                universityId = univ.getUniversityId();
                            }

                        }

                        String url = urlGen.getAdvancedSearchAccommodationAdds(apartmentName, gender, universityId);

                        if ((!aptNamesVal.equals(apartmentName) || !sexVal.equals(gender) || !univVal.equals(universityVal)) && universityId != -1) {


                            L.m("came inside");

                            getFromServer(url);

                            aptNamesVal = apartmentName;
                            sexVal = gender;

                        }


                    } catch (Exception e) {

                        ErrorReporting.logReport(e);
                    }

                }
            });
        } catch (Exception e) {
            ErrorReporting.logReport(e);
        }

    }


    public void setSpinners() {
        // 1st spinner - apartment Type- ( On campus, off, dorms)
        aptTypes = (Spinner) pageView.findViewById(R.id.aptTypeSpinnerSearch);
        aptTypes.setAdapter(createArrayAdapter(R.array.apartment_type));
        aptTypes.setSelection(0, false);
        aptTypes.setOnItemSelectedListener(this);


        // 2nd spinner - apt names
        aptNames = (Spinner) pageView.findViewById(R.id.aptNameSpinnerSearch);
        aptNames.setAdapter(createArrayAdapter(new ArrayList<String>()));
        aptNamesAdapter = (ArrayAdapter<String>) aptNames.getAdapter();

        // 3rd spinner - occupant sex
        sex = (Spinner) pageView.findViewById(R.id.occcupantSexSpinnerSearch);
        sex.setAdapter(createArrayAdapter(R.array.occcupant_gender));


        universityNames = (Spinner) pageView.findViewById(R.id.universityNamesSpinner);
        universityNames.setAdapter(createArrayAdapter(new ArrayList<String>()));
        universityNamesAdapter = (ArrayAdapter<String>) universityNames.getAdapter();
        universityNamesAdapter.add("");
        universityNames.setSelection(0, false);
        universityNames.setOnItemSelectedListener(this);

    }


    public void setmRecyclerVIew() {

        try {

            mRecyclerVIew = (RecyclerView) pageView.findViewById(R.id.advSearchRecyclerView);
            mRecyclerVIew.setLayoutManager(new LinearLayoutManager(pageView.getContext()));

            mAccommodationAddsAdapter = new AccommodationAddsAdapterLoader(pageView.getContext(), new ArrayList<AccommodationAdd>(), this, mRecyclerVIew);

            mRecyclerVIew.setAdapter(mAccommodationAddsAdapter);

            mAccommodationAddsAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
                @Override
                public void onLoadMore(int position) {

                    mAccommodationAddsAdapter.add(null);

                    String paginationUrl = UrlGenerator.getPaginationUrl(recentUrl, position);

                    new AccommodationBO(paginationUrl, new AccommodationBI() {
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


        } catch (Exception e) {
            ErrorReporting.logReport(e);
        }

    }

    private void addToRecyclerView(ArrayList<AccommodationAdd> advertisements) {

        try {

            mAccommodationAddsAdapter.addAll(advertisements);
        } catch (Exception e) {
            ErrorReporting.logReport(e);
        }
    }

    // Creating adapter for the spinner with strings returned from the server
    public ArrayAdapter<String> createArrayAdapter(List<String> list) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this.getActivity(), android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }

    // Creating adapter for the spinner with strings returned from the resources
    public ArrayAdapter<CharSequence> createArrayAdapter(int id) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this.getActivity(), id, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        return adapter;
    }

    /**
     * Populate apartmentNames spinner and frame accommodationAdds Recycler View URL
     *
     * @param parent
     * @param spinnerView
     * @param position
     * @param spinnerId
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View spinnerView, int position,
                               long spinnerId) {

        try {

            if (parent.getId() == R.id.aptTypeSpinnerSearch || parent.getId() == R.id.universityNamesSpinner) {
                addApartmentNames();
            }
        } catch (Exception e) {
            ErrorReporting.logReport(e);
        }
    }

    /**
     * get the list of all apartment names with type and loops around it to get populate either on/off campus apartments into aptNames spinner
     */
    private void addApartmentNames() {

        aptNamesAdapter.clear();
        for (ApartmentNamesInUnivs university : apartmentNamesInUnivs) {

            if (university.getUniversityName().equals(universityNames.getSelectedItem().toString())) {

                for (ApartmentNamesWithType aptName : university.getApartmentNames()) {

                    if (aptName.getApartmentType().equals(UrlGenerator.getApartmentTypeCodeMap().get(aptTypes.getSelectedItem().toString())))

                        aptNamesAdapter.add(aptName.getApartmentName());
                }

            }
        }
    }


    private void getApartmentNames() {

        StudentAssistBO manager = new StudentAssistBO();
        manager.volleyRequest(urlGen.getApartmentNamesUrl(""), new NetworkInterface() {
            @Override
            public void onResponseUpdate(String jsonResponse) {
                try {

                    Gson gson = new Gson();
                    universityNamesAdapter.remove("");

                    apartmentNamesInUnivs = gson.fromJson(jsonResponse, new TypeToken<List<ApartmentNamesInUnivs>>() {
                    }.getType());
                    int count = 0;




                    for (ApartmentNamesInUnivs university : apartmentNamesInUnivs) {
                        universityNamesAdapter.add(university.getUniversityName());


                        if (count == 0) {

                            //adding apartment names as per AptType(on/off campus)
                            addApartmentNames();
                        }

                        count++;

                    }


                } catch (Exception e) {
                    ErrorReporting.logReport(e);

                }
            }
        }, "", Request.Method.GET);


    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {

    }


    private void getFromServer(String url) {

        recentUrl = url;

        try {


            new AccommodationBO(url, new AccommodationBI() {
                @Override
                public void onAccommodationAddsReady(ArrayList<AccommodationAdd> advertisements) {

                    populateRecyclerView(advertisements);

                }

                ///not needed
                @Override
                public void onApartmentNamesReady(ArrayList<String> apartmentNames) {

                }


            }, SAConstants.ACCOMMODATION_ADDS);


        } catch (Exception e) {
            ErrorReporting.logReport(e);
        }
    }

    private void populateRecyclerView(ArrayList<AccommodationAdd> advertisements) {

        Utilities.hideView(pageView, R.id.loader);
        mAccommodationAddsAdapter.clear();
        mAccommodationAddsAdapter.addAll(advertisements);
        adds.clear();
        adds.addAll(advertisements);
    }

    @Override
    public void onTouch(AccommodationAdd add, View view) {
        Intent details = new Intent(getActivity(), AdDetailsActivity.class);
        details.putExtra(SAConstants.ACCOMMODATION_ADD_PARCELABLE, (Parcelable) add);
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(getActivity(), (View) view, "profile");

        details.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivityForResult(details, 1, options.toBundle());

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (aptNames != null) {
            outState.putParcelableArrayList(SAConstants.STATE_CHANGED, adds);
            outState.putParcelableArrayList(SAConstants.APARTMENT_NAMES, apartmentNamesInUnivs);


            outState.putInt(SAConstants.APARTMENT_NAME_POSITION, aptNames.getSelectedItemPosition());
            outState.putInt(SAConstants.APARTMENT_TYPE_POSITION, aptTypes.getSelectedItemPosition());
            outState.putInt(SAConstants.GENDER_POSITION, sex.getSelectedItemPosition());
            outState.putInt(SAConstants.UNIVERSITY_NAME_POSITION, universityNames.getSelectedItemPosition());

        }
    }


}