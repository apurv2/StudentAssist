
package com.apurv.studentassist.accommodation.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
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
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;

import com.apurv.studentassist.R;
import com.apurv.studentassist.accommodation.Interfaces.AccommodationActivityToFragmentInterface;
import com.apurv.studentassist.accommodation.Interfaces.AccommodationBI;
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
import java.util.Collections;
import java.util.List;

public class SearchAccomodationFragment extends Fragment implements
        OnItemSelectedListener, RecyclerTouchInterface {


    private View pageView;
    Spinner leftSpinner, rightSpinner;
    UrlInterface urlGen = new UrlGenerator();


    List<AccommodationAddsAdapterLoader> accommodationAddAdaptersList;
    List<CardView> cardViewsList = new ArrayList<>();
    List<LinearLayout> loaderList = new ArrayList();

    private AccommodationAddsAdapterLoader mAccommodationAddsAdapter1;
    private AccommodationAddsAdapterLoader mAccommodationAddsAdapter2;
    private AccommodationAddsAdapterLoader mAccommodationAddsAdapter3;
    private AccommodationAddsAdapterLoader mAccommodationAddsAdapter4;

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
        setmRecyclerView();
        setSpinners();


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


    public void setmRecyclerView() {

        try {


            RecyclerView mRecyclerVIew1;
            RecyclerView mRecyclerVIew2;
            RecyclerView mRecyclerVIew3;
            RecyclerView mRecyclerVIew4;

            mRecyclerVIew1 = (RecyclerView) pageView.findViewById(R.id.addslist1);
            mRecyclerVIew2 = (RecyclerView) pageView.findViewById(R.id.addslist2);
            mRecyclerVIew3 = (RecyclerView) pageView.findViewById(R.id.addslist3);
            mRecyclerVIew4 = (RecyclerView) pageView.findViewById(R.id.addslist4);

            mRecyclerVIew1.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
            mRecyclerVIew2.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
            mRecyclerVIew3.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
            mRecyclerVIew4.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));


            mAccommodationAddsAdapter1 = new AccommodationAddsAdapterLoader(pageView.getContext(), new ArrayList<AccommodationAdd>(), this, mRecyclerVIew1);
            mAccommodationAddsAdapter2 = new AccommodationAddsAdapterLoader(pageView.getContext(), new ArrayList<AccommodationAdd>(), this, mRecyclerVIew2);
            mAccommodationAddsAdapter3 = new AccommodationAddsAdapterLoader(pageView.getContext(), new ArrayList<AccommodationAdd>(), this, mRecyclerVIew3);
            mAccommodationAddsAdapter4 = new AccommodationAddsAdapterLoader(pageView.getContext(), new ArrayList<AccommodationAdd>(), this, mRecyclerVIew4);


            mRecyclerVIew1.setAdapter(mAccommodationAddsAdapter1);
            mRecyclerVIew2.setAdapter(mAccommodationAddsAdapter2);
            mRecyclerVIew3.setAdapter(mAccommodationAddsAdapter3);
            mRecyclerVIew4.setAdapter(mAccommodationAddsAdapter4);

            accommodationAddAdaptersList = new ArrayList();
            accommodationAddAdaptersList.add(mAccommodationAddsAdapter1);
            accommodationAddAdaptersList.add(mAccommodationAddsAdapter2);
            accommodationAddAdaptersList.add(mAccommodationAddsAdapter3);
            accommodationAddAdaptersList.add(mAccommodationAddsAdapter4);

            cardViewsList.add((CardView) pageView.findViewById(R.id.cardView1));
            cardViewsList.add((CardView) pageView.findViewById(R.id.cardView2));
            cardViewsList.add((CardView) pageView.findViewById(R.id.cardView3));
            cardViewsList.add((CardView) pageView.findViewById(R.id.cardView4));

            loaderList.add((LinearLayout) pageView.findViewById(R.id.loadingPanel1));
            loaderList.add((LinearLayout) pageView.findViewById(R.id.loadingPanel2));
            loaderList.add((LinearLayout) pageView.findViewById(R.id.loadingPanel3));
            loaderList.add((LinearLayout) pageView.findViewById(R.id.loadingPanel4));




           /* mAccommodationAddsAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
                @Override
                public void onLoadMore(int position) {

                    mAccommodationAddsAdapter.add(null);


                    new AccommodationBO(UrlGenerator.getPaginationUrl(recentUrl, position), new AccommodationBI() {
                        @Override
                        public void onAccommodationAddsReady(ArrayList<AccommodationAdd> advertisements) {

                            processAccommodationAdds(advertisements);
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
            });*/


        } catch (Exception e) {
            ErrorReporting.logReport(e);
        }

    }


    private void processAccommodationAdds(List<AccommodationAdd> advertisements) {

        Collections.sort(advertisements, AccommodationAdd.getComparatorByUnivId());

        int counter = 0;
        List<List<AccommodationAdd>> accommodationAddsLists = new ArrayList<>();
        List<AccommodationAdd> tempList = new ArrayList<>();

        for (AccommodationAdd accAdd : advertisements) {

            if (counter > 0 && accAdd.getUniversityId() != advertisements.get(counter - 1).getUniversityId()) {
                accommodationAddsLists.add(tempList);
                tempList = new ArrayList<>();
            }
            if (counter == advertisements.size() - 1) {
                accommodationAddsLists.add(tempList);
            }
            tempList.add(accAdd);
            counter++;
        }
        populateRecyclerView(accommodationAddsLists);

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
                            processAccommodationAdds(adds);

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
                        processAccommodationAdds(adds);

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

                //Utilities.showView(pageView, R.id.loadingPanel);


                if (false) {
                    reEntryFlag = false;

                    L.m("populating from bundle view");
                    processAccommodationAdds(bundle.<AccommodationAdd>getParcelableArrayList(SAConstants.STATE_CHANGED));

                } else {

                    L.m("search accommodation fragment requresting simple search addds");
                    new AccommodationBO(url, new AccommodationBI() {
                        @Override
                        public void onAccommodationAddsReady(ArrayList<AccommodationAdd> advertisements) {

                            L.m("populating from remote server");
                            reEntryFlag = false;

                            adds.clear();
                            adds.addAll(advertisements);
                            processAccommodationAdds(advertisements);

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

    private void populateRecyclerView(List<List<AccommodationAdd>> accommodationAddsList) {

        try {
            L.m(accommodationAddsList + "");

            int listSize = accommodationAddsList.size();
            LinearLayout firstChild = (LinearLayout) pageView.findViewById(R.id.childLayout);
            ScrollView sv = (ScrollView) pageView.findViewById(R.id.scrollView);


           /* if (listSize == 1 && sv!=null) {
                ViewGroup parentLayout = ((ViewGroup) sv.getParent());

                sv.removeView(firstChild); //Removing cardView from scrollview
                parentLayout.addView(firstChild); // adding that removed view to scrollView's parent
                parentLayout.removeView(sv);

            } else if (listSize > 1) {
                ScrollView scroll = new ScrollView(pageView.getContext());
                scroll.setId(R.id.scrollView);
                scroll.setLayoutParams(new ScrollView.LayoutParams(ScrollView.LayoutParams.MATCH_PARENT,
                        ScrollView.LayoutParams.MATCH_PARENT));

                ((ViewGroup) firstChild.getParent()).removeView(firstChild);
                scroll.addView(firstChild);
            }*/


            for (int i = 0; i < accommodationAddAdaptersList.size(); i++) {


                if (i < listSize) {
                    accommodationAddAdaptersList.get(i).clear();
                    accommodationAddAdaptersList.get(i).addAll(accommodationAddsList.get(i));
                    Utilities.hideView(loaderList.get(i));

                    //shows card views from 1 to length of adapterslist
                    if (i > 0) {
                        Utilities.showView(cardViewsList.get(i));
                    }
                }
            }



          /*  Utilities.hideView(pageView, R.id.loadingPanel);
            mAccommodationAddsAdapter.clear();
            mAccommodationAddsAdapter.addAll(advertisements);*/


        } catch (Exception e) {
            ErrorReporting.logReport(e);
        }
    }

    //commented out temporarily
    //used to add results after pagination
   /* private void addToRecyclerView(ArrayList<AccommodationAdd> advertisements) {

        try {

            mAccommodationAddsAdapter.addAll(advertisements);
        } catch (Exception e) {
            ErrorReporting.logReport(e);
        }
    }
*/

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

