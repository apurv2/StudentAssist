
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.apurv.studentassist.R;
import com.apurv.studentassist.accommodation.Interfaces.AccommodationActivityToFragmentInterface;
import com.apurv.studentassist.accommodation.Interfaces.AccommodationAddsRecyclerInterface;
import com.apurv.studentassist.accommodation.Interfaces.AccommodationBI;
import com.apurv.studentassist.accommodation.activities.AccommodationActivity;
import com.apurv.studentassist.accommodation.activities.AdDetailsActivity;
import com.apurv.studentassist.accommodation.activities.NotificationSettingsActivity;
import com.apurv.studentassist.accommodation.adapters.AccommodationAddsAdapterLoader;
import com.apurv.studentassist.accommodation.business.rules.AccommodationBO;
import com.apurv.studentassist.accommodation.classes.AccommodationAdd;
import com.apurv.studentassist.accommodation.urlInfo.UrlGenerator;
import com.apurv.studentassist.accommodation.urlInfo.UrlInterface;
import com.apurv.studentassist.util.ErrorReporting;
import com.apurv.studentassist.util.L;
import com.apurv.studentassist.util.SAConstants;
import com.apurv.studentassist.util.Utilities;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SearchAccomodationFragment extends Fragment implements
        OnItemSelectedListener, AccommodationAddsRecyclerInterface {


    private View pageView;
    UrlInterface urlGen = new UrlGenerator();

    List<List<AccommodationAdd>> accommodationAddsListGolbal = new ArrayList();
    List<AccommodationAddsAdapterLoader> accommodationAddAdaptersList = new ArrayList();
    List<CardView> cardViewsList = new ArrayList<>();
    List<LinearLayout> loaderList = new ArrayList();
    List<ImageView> imageViewsList = new ArrayList();
    List<TextView> textViewList = new ArrayList();
    List<RecyclerView> recyclerViews = new ArrayList<>();

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

    @Bind(R.id.universityName1)
    TextView universityName1;

    @Bind(R.id.universityName2)
    TextView universityName2;

    @Bind(R.id.universityName3)
    TextView universityName3;

    @Bind(R.id.universityName4)
    TextView universityName4;

    @Bind(R.id.imageView1)
    ImageView imageView1;

    @Bind(R.id.imageView2)
    ImageView imageView2;

    @Bind(R.id.imageView3)
    ImageView imageView3;

    @Bind(R.id.imageView4)
    ImageView imageView4;


    @Bind(R.id.addslist1)
    RecyclerView mRecyclerVIew1;

    @Bind(R.id.addslist2)
    RecyclerView mRecyclerVIew2;

    @Bind(R.id.addslist3)
    RecyclerView mRecyclerVIew3;

    @Bind(R.id.addslist4)
    RecyclerView mRecyclerVIew4;

    @Bind(R.id.cardView1)
    CardView cardView1;

    @Bind(R.id.cardView2)
    CardView cardView2;

    @Bind(R.id.cardView3)
    CardView cardView3;

    @Bind(R.id.cardView4)
    CardView cardView4;

    @Bind(R.id.loadingPanel1)
    LinearLayout loadingPanel1;

    @Bind(R.id.loadingPanel2)
    LinearLayout loadingPanel2;

    @Bind(R.id.loadingPanel3)
    LinearLayout loadingPanel3;

    @Bind(R.id.loadingPanel4)
    LinearLayout loadingPanel4;

    @Bind(R.id.leftSpinner)
    Spinner leftSpinner;

    @Bind(R.id.rightSpinner)
    Spinner rightSpinner;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        //Setting up the parent view using Layout Inflation
        pageView = inflater.inflate(R.layout.activity_search_accomodation, container, false);
        ButterKnife.bind(this, pageView);

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
        setLists();
        setmRecyclerView();
        setSpinners();


        setHasOptionsMenu(true);


        pageView.findViewById(R.id.header1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Utilities.toggleView(mRecyclerVIew1);
            }
        });

        pageView.findViewById(R.id.header2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilities.toggleView(mRecyclerVIew2);
            }
        });
        pageView.findViewById(R.id.header3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilities.toggleView(mRecyclerVIew3);
            }
        });
        pageView.findViewById(R.id.header4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilities.toggleView(mRecyclerVIew4);
            }
        });

        return pageView;
    }


    private void setLists() {
        cardViewsList.clear();
        cardViewsList.add(cardView1);
        cardViewsList.add(cardView2);
        cardViewsList.add(cardView3);
        cardViewsList.add(cardView4);

        loaderList.clear();
        loaderList.add(loadingPanel1);
        loaderList.add(loadingPanel2);
        loaderList.add(loadingPanel3);
        loaderList.add(loadingPanel4);

        imageViewsList.clear();
        imageViewsList.add(imageView1);
        imageViewsList.add(imageView2);
        imageViewsList.add(imageView3);
        imageViewsList.add(imageView4);

        textViewList.clear();
        textViewList.add(universityName1);
        textViewList.add(universityName2);
        textViewList.add(universityName3);
        textViewList.add(universityName4);

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
        rightSpinner = (Spinner) pageView.findViewById(R.id.rightSpinner);
        rightSpinner.setAdapter(createArrayAdapter(new ArrayList<String>()));

        leftSpinner.setOnItemSelectedListener(this);
        rightSpinner.setOnItemSelectedListener(this);

    }


    public void setmRecyclerView() {

        try {


            mRecyclerVIew1.setNestedScrollingEnabled(false);
            mRecyclerVIew2.setNestedScrollingEnabled(false);
            mRecyclerVIew3.setNestedScrollingEnabled(false);
            mRecyclerVIew4.setNestedScrollingEnabled(false);

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

            recyclerViews.clear();
            recyclerViews.add(mRecyclerVIew1);
            recyclerViews.add(mRecyclerVIew2);
            recyclerViews.add(mRecyclerVIew3);
            recyclerViews.add(mRecyclerVIew4);

            accommodationAddAdaptersList.clear();
            accommodationAddAdaptersList.add(mAccommodationAddsAdapter1);
            accommodationAddAdaptersList.add(mAccommodationAddsAdapter2);
            accommodationAddAdaptersList.add(mAccommodationAddsAdapter3);
            accommodationAddAdaptersList.add(mAccommodationAddsAdapter4);




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

        if (!(leftSpinner.getSelectedItem().toString().equals(historyLeftSpinner) && rightSpinner.getSelectedItem().toString().equals(historyRightSpinner))) {

            historyLeftSpinner = leftSpinner.getSelectedItem().toString();
            historyRightSpinner = rightSpinner.getSelectedItem().toString();

            Collections.sort(advertisements, AccommodationAdd.getComparatorByUnivId());

            int counter = 0;
            List<List<AccommodationAdd>> accommodationAddsLists = new ArrayList<>();
            List<AccommodationAdd> tempList = new ArrayList<>();

            for (AccommodationAdd accAdd : advertisements) {

                if (counter > 0 && (accAdd.getUniversityId() != advertisements.get(counter - 1).getUniversityId())) {
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

                                                                rightSpinner.post(new Runnable() {
                                                                    public void run() {
                                                                        rightSpinner.setSelection(bundle.getInt(SAConstants.RIGHT_SPINNER), false);
                                                                    }
                                                                });

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
                        String rightSpinnerValue="";
                        if (reEntryFlag) {

                            rightSpinner.post(new Runnable() {
                                public void run() {
                                    rightSpinner.setSelection(bundle.getInt(SAConstants.RIGHT_SPINNER), false);
                                }
                            });
                            rightSpinnerValue = rightSpinnerValues[bundle.getInt(SAConstants.RIGHT_SPINNER)];
                        } else {
                            rightSpinner.setSelection(0, false);
                            rightSpinnerValue = rightSpinnerValues[0];

                        }
                        accommodationAddsUrl = urlGen.getSearchAccommodationAdds(leftSpinner.getSelectedItem().toString(), rightSpinnerValue);
                        //  if (adds.size() < 1) {
                        getFromServer(accommodationAddsUrl, leftSpinner.getSelectedItem().toString(), rightSpinnerValue);
                        //   } else {
                        //       processAccommodationAdds(adds);

                        // }
                    }

                }

                //Right Spinner selected
            } else {


                String rightSpinnerValue = rightSpinner.getSelectedItem().toString();

                // Checking firing of onItemCLick listener for same value
                if (!rightSpinnerSameVal.equals(rightSpinnerValue)) {

                    rightSpinnerSameVal = rightSpinnerValue;

                    accommodationAddsUrl = urlGen.getSearchAccommodationAdds(leftSpinner.getSelectedItem().toString(), rightSpinnerValue);
                    if (reEntryFlag && !adds.isEmpty()) {
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

                //  historyLeftSpinner = left;
                //historyRightSpinner = right;
                //Utilities.showView(pageView, R.id.loadingPanel);

                L.m("search accommodation fragment requresting simple search addds");
                new AccommodationBO(url, new AccommodationBI() {
                    @Override
                    public void onAccommodationAddsReady(ArrayList<AccommodationAdd> advertisements) {

                        L.m("populating from remote server");
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


        } catch (Exception e) {
            ErrorReporting.logReport(e);
        }
    }

    private void populateRecyclerView(List<List<AccommodationAdd>> accommodationAddsList) {

        accommodationAddsListGolbal = accommodationAddsList;
        try {
            L.m(accommodationAddsList + "");

            int listSize = accommodationAddsList.size();

            for (int i = 0; i < accommodationAddAdaptersList.size(); i++) {
                if (i < listSize) {

                    if (!reEntryFlag || accommodationAddAdaptersList.get(i).isEmpty()) {
                        accommodationAddAdaptersList.get(i).clear();
                        accommodationAddAdaptersList.get(i).addAll(accommodationAddsList.get(i));

                    }

                    Utilities.hideView(loaderList.get(i));


                    AccommodationAdd add = accommodationAddsList.get(i).get(0);

                    //shows card views to length of adapterslist
                    Utilities.showView(cardViewsList.get(i));
                    Utilities.loadImages(add.getPhotoUrl(), imageViewsList.get(i), textViewList.get(i));
                    textViewList.get(i).setText(add.getUniversityName());

                } else {

                    L.m("hiding view==" + i + "id===" + cardViewsList.get(i).getId());
                    Utilities.hideView(cardViewsList.get(i));
                }

            }
          /*  Utilities.hideView(pageView, R.id.loadingPanel);
            mAccommodationAddsAdapter.clear();
            mAccommodationAddsAdapter.addAll(advertisements);*/
            reEntryFlag = false;

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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.subscribe, menu);


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

    @Override
    public void onTouch(AccommodationAdd add, View view) {

        Intent details = new Intent(getActivity(), AdDetailsActivity.class);
        add.setUserVisitedSw(true);

        details.putExtra(SAConstants.ACCOMMODATION_ADD_PARCELABLE, (Parcelable) add);
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(getActivity(), (View) view, "profile");

        details.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivityForResult(details, 1, options.toBundle());

    }
}



