
package com.apurv.studentassist.accommodation.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.Spinner;

import com.apurv.studentassist.R;
import com.apurv.studentassist.accommodation.Dialogs.SimpleSubscriptionAlertDialog;
import com.apurv.studentassist.accommodation.Interfaces.AccommodationActivityToFragmentInterface;
import com.apurv.studentassist.accommodation.Interfaces.AccommodationBI;
import com.apurv.studentassist.accommodation.Interfaces.OnLoadMoreListener;
import com.apurv.studentassist.accommodation.activities.AccommodationActivity;
import com.apurv.studentassist.accommodation.activities.AdDetailsActivity;
import com.apurv.studentassist.accommodation.activities.NotificationSettingsActivity;
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

public class SearchAccomodationFragment extends Fragment implements
        OnItemSelectedListener, RecyclerTouchInterface {


    private View pageView;
    Spinner leftSpinner, rightSpinner;
    UrlInterface urlGen = new UrlGenerator();
    private RecyclerView mRecyclerVIew;
    private AccommodationAddsAdapterLoader mAccommodationAddsAdapter;
    ArrayList<AccommodationAdd> adds = new ArrayList<AccommodationAdd>();
    String historyLeftSpinner = "", historyRightSpinner = "";
    String leftSpinnerSameVal = "", rightSpinnerSameVal = "";
    String recentUrl = "";

    Bundle bundle;
    boolean reEntryFlag = false;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        //Setting up the parent view using Layout Inflation
        pageView = inflater.inflate(R.layout.activity_search_accomodation, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Easy Search");


        setRetainInstance(true);


        //reEntry flag is true if device is rotated or fragment state is restored
        if (savedInstanceState != null) {
            bundle = savedInstanceState;
            reEntryFlag = true;

        } else {

            bundle = null;
            reEntryFlag = false;
        }


        // Initialize and set Spinners
        setSpinners();
        setmRecyclerVIew();

        setHasOptionsMenu(true);

        return pageView;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.subscribe, menu);


    }

    /**
     * @param item
     * @return adding subscribe button
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.ic_subscribe:
                Intent subscriptionIntent = new Intent(getActivity(), NotificationSettingsActivity.class
                );
                startActivity(subscriptionIntent);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Alert the user about subscription
     */
    private void notificationAlert() {

        Bundle bundle = new Bundle();

        String left = leftSpinner.getSelectedItem().toString();
        String right = rightSpinner.getSelectedItem().toString();


        bundle.putString(SAConstants.ALERT_TEXT, left + " : " + right);
        SimpleSubscriptionAlertDialog subscriptionAlert = new SimpleSubscriptionAlertDialog();
        subscriptionAlert.setArguments(bundle);
        subscriptionAlert.setTargetFragment(this, 0);
        subscriptionAlert.show(getActivity().getSupportFragmentManager(), "");


    }


    public void setSpinners() {
        // data from local
        leftSpinner = (Spinner) pageView.findViewById(R.id.leftSpinner);
        leftSpinner.setAdapter(createArrayAdapter(R.array.search_parameters));


        // data from server
        rightSpinner = (Spinner) pageView.findViewById(R.id.value_spinner);
        rightSpinner.setAdapter(createArrayAdapter(new ArrayList<String>()));

        leftSpinner.setOnItemSelectedListener(this);
        rightSpinner.setOnItemSelectedListener(this);

    }


    public void setmRecyclerVIew() {

        try {
            mRecyclerVIew = (RecyclerView) pageView.findViewById(R.id.addslist);
            mRecyclerVIew.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));

            //old adapter
            //  mAccommodationAddsAdapter = new AccommodationAddsAdapter(pageView.getContext(), new ArrayList<AccommodationAdd>(), this);


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


        } catch (Exception e) {
            ErrorReporting.logReport(e);
        }

    }

    // Creating adapter for the spinner with strings returned from the server
    public ArrayAdapter<String> createArrayAdapter(List<String> List) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this.getActivity(), android.R.layout.simple_spinner_item, List);
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
     * Populate right spinner and frame accommodationAdds Recycler View URL
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

            String accommodationAddsUrl = "";

            // left spinner selected
            if (parent.getId() == R.id.leftSpinner) {


                String leftSpinnerValue = leftSpinner.getSelectedItem().toString();

                // condition to check for indefinite firing of onItemSelected listener. If the onClick is called with the spinner
                //containing the same selected value, this if condition filters it
                if (!leftSpinnerSameVal.equals(leftSpinnerValue)) {

                    leftSpinnerSameVal = leftSpinnerValue;

                    // populating the right side spinner
                    final ArrayAdapter<String> rightSpinnerAdapter = (ArrayAdapter<String>) rightSpinner.getAdapter();
                    rightSpinnerAdapter.clear();


                    //Populating the spinners from data that is present in the Activity.
                    final AccommodationActivity mActivity = (AccommodationActivity) getActivity();
                    final String rightSpinnerValues[] = mActivity.spinnerInformation.get(parent.getItemAtPosition(position));


                    // When apartmentName is selected in left Spinner. We need to populate the right spinner with Apartment Names. These are fetched
                    //from the parent Activity.
                    if (rightSpinnerValues == null || rightSpinnerValues.length < 1) {

                        // Response from the parnet activity containing data to load the spinners with.
                        mActivity.getApartmentNames(new AccommodationActivityToFragmentInterface() {
                                                        @Override
                                                        public void onApartmentNamesListReady(String[] apartmentStringArray) {


                                                            rightSpinnerAdapter.addAll(apartmentStringArray);
                                                            rightSpinnerAdapter.notifyDataSetChanged();

                                                            if (reEntryFlag) {
                                                                rightSpinner.setSelection(bundle.getInt(SAConstants.RIGHT_SPINNER), false);
                                                            } else {
                                                                rightSpinner.setSelection(0, false);
                                                            }


                                                        }
                                                    }

                        );

                        // Else condition when ApartmentType/Gender is selected in the left spinner
                    } else {

                        // adding the data to spinner adapters
                        rightSpinnerAdapter.addAll(rightSpinnerValues);
                        rightSpinnerAdapter.notifyDataSetChanged();

                        //Setting the selected item of the spinner to restore the previous state of fragment.
                        if (reEntryFlag) {
                            rightSpinner.setSelection(bundle.getInt(SAConstants.RIGHT_SPINNER), false);
                        } else {
                            rightSpinner.setSelection(0, false);
                        }
                        String rightSpinnerValue = rightSpinner.getSelectedItem().toString();
                        accommodationAddsUrl = urlGen.getSearchAccommodationAdds(leftSpinner.getSelectedItem().toString(), rightSpinnerValue);
                        if (adds.size() < 1) {
                            getFromServer(accommodationAddsUrl, leftSpinner.getSelectedItem().toString(), rightSpinnerValue);
                        } else {
                            populateRecyclerView(adds);

                        }
                    }

                }

                //Right Spinner selected
            } else {


                String rightSpinnerValue = rightSpinner.getSelectedItem().toString();

                // Checking firing of onItemCLick listener for same value
                if (!rightSpinnerSameVal.equals(rightSpinnerValue)) {

                    rightSpinnerSameVal = rightSpinnerValue;

                    accommodationAddsUrl = urlGen.getSearchAccommodationAdds(leftSpinner.getSelectedItem().toString(), rightSpinnerValue);
                    if (reEntryFlag) {
                        populateRecyclerView(adds);

                    } else {
                        getFromServer(accommodationAddsUrl, leftSpinner.getSelectedItem().toString(), rightSpinnerValue);

                    }
                }

            }
        } catch (UnsupportedEncodingException e) {
            ErrorReporting.logReport(e);
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {

    }

    public void refresh() {
        L.m("refreshing page after posting acc");
        try {
            getFromServer(recentUrl, leftSpinner.getSelectedItem().toString(), rightSpinner.getSelectedItem().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void getFromServer(String url, String left, String right) {

        recentUrl = url;

        try {

            // Checking if the server call happens only once for the same parameters. It does not however store the previous history.
            if (!(left.equals(historyLeftSpinner) && right.equals(historyRightSpinner))) {
                historyLeftSpinner = left;
                historyRightSpinner = right;

                Utilities.showView(pageView, R.id.loadingPanel);


                if (false) {
                    reEntryFlag = false;

                    L.m("populating from bundle view");
                    populateRecyclerView(bundle.<AccommodationAdd>getParcelableArrayList(SAConstants.STATE_CHANGED));

                } else {

                    L.m("search accommodation fragment requresting simple search addds");
                    new AccommodationBO(url, new AccommodationBI() {
                        @Override
                        public void onAccommodationAddsReady(ArrayList<AccommodationAdd> advertisements) {

                            L.m("populating from remote server");
                            reEntryFlag = false;

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

            }


        } catch (Exception e) {
            ErrorReporting.logReport(e);
        }
    }

    private void populateRecyclerView(ArrayList<AccommodationAdd> advertisements) {

        try {


            Utilities.hideView(pageView, R.id.loadingPanel);
            mAccommodationAddsAdapter.clear();
            mAccommodationAddsAdapter.addAll(advertisements);


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


    @Override
    public void onTouch(int position, View view) {


        Intent details = new Intent(getActivity(), AdDetailsActivity.class);
        adds.get(position).setUserVisitedSw(true);

        details.putExtra(SAConstants.ACCOMMODATION_ADD_PARCELABLE, (Parcelable) adds.get(position));
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(getActivity(), (View) view, "profile");

        details.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivityForResult(details, 1, options.toBundle());
    }

    @Override
    public void onPause() {
        super.onPause();

        historyLeftSpinner = "";
        historyRightSpinner = "";
        leftSpinnerSameVal = "";
        rightSpinnerSameVal = "";

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);


    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (leftSpinner != null || rightSpinner != null) {

            outState.putParcelableArrayList(SAConstants.STATE_CHANGED, adds);
            outState.putInt(SAConstants.LEFT_SPINNER, leftSpinner.getSelectedItemPosition());
            outState.putInt(SAConstants.RIGHT_SPINNER, rightSpinner.getSelectedItemPosition());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        AccommodationActivity parentActivity = (AccommodationActivity) getActivity();
        if (parentActivity.postedAccommodation) {
            parentActivity.postedAccommodation = false;
            refresh();
        }


    }
}