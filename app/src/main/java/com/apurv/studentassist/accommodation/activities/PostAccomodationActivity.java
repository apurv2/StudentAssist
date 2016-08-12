package com.apurv.studentassist.accommodation.activities;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Base64;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.apurv.studentassist.R;
import com.apurv.studentassist.accommodation.Dialogs.AccommodationPosted;
import com.apurv.studentassist.accommodation.Dialogs.AlertDialogL;
import com.apurv.studentassist.accommodation.Dialogs.LoadingDialog;
import com.apurv.studentassist.accommodation.Dialogs.NewApartmentDialog;
import com.apurv.studentassist.accommodation.Interfaces.AccommodationBI;
import com.apurv.studentassist.accommodation.Interfaces.PostAccommodationBI;
import com.apurv.studentassist.accommodation.business.rules.AccommodationBO;
import com.apurv.studentassist.accommodation.classes.AccommodationAdd;
import com.apurv.studentassist.accommodation.classes.User;
import com.apurv.studentassist.accommodation.urlInfo.UrlGenerator;
import com.apurv.studentassist.accommodation.urlInfo.UrlInterface;
import com.apurv.studentassist.util.Alerts;
import com.apurv.studentassist.util.ErrorReporting;
import com.apurv.studentassist.util.GUIUtils;
import com.apurv.studentassist.util.L;
import com.apurv.studentassist.util.ObjectSerializer;
import com.apurv.studentassist.util.SAConstants;
import com.apurv.studentassist.util.Utilities;
import com.apurv.studentassist.util.interfaces.LodingDialogInterface;
import com.apurv.studentassist.util.interfaces.OnRevealAnimationListener;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PostAccomodationActivity extends AppCompatActivity implements
        OnItemSelectedListener, LodingDialogInterface {

    @Bind(R.id.activity_contact_rl_container)
    RelativeLayout mRlContainer;
    @Bind(R.id.activity_contact_fab)
    FloatingActionButton mFab;

    @Bind(R.id.parent)
    LinearLayout mLlContainer;


    UrlInterface urlGen = new UrlGenerator();
    Spinner apartmentTypeSpinner, noOfRoomsSpinner, noOfVacanciesSpinner, occupantSexSpinner, apartmentNameSpinner;
    List<String> errorQueue = new ArrayList<String>();
    SharedPreferences sharedPreferences;
    private View pageView;
    Bundle bundle;
    Boolean reEntryFlag = false;
    ArrayList<String> mApartmentNames;
    String aptTypeSpinnerVal = "";


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_accomodation);
        pageView = findViewById(R.id.parent);

        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setupEnterAnimation();
            setupExitAnimation();
        } else {
            initViews();
        }

        if (savedInstanceState != null) {

            bundle = savedInstanceState;
            reEntryFlag = true;
        }

        // setup toolbar
        Toolbar toolbar;
        toolbar = (Toolbar) findViewById(R.id.applicationBar);
        toolbar.setTitle(SAConstants.POST_ACCOMMODATION);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //set spinners
        setSpinners();

        final Context context = this;

        final Button postVacancy = (Button) pageView.findViewById(R.id.postVacancy);
        postVacancy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateAll()) {


                    postVacancy();

                }
            }
        });

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setupEnterAnimation() {
        Transition transition = TransitionInflater.from(this)
                .inflateTransition(R.transition.changebounds_with_arcmotion);
        getWindow().setSharedElementEnterTransition(transition);
        transition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {

            }

            @Override
            public void onTransitionEnd(Transition transition) {
                transition.removeListener(this);
                animateRevealShow(mRlContainer);
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }
        });
    }

    private void animateRevealShow(final View viewRoot) {
        int cx = (viewRoot.getLeft() + viewRoot.getRight()) / 2;
        int cy = (viewRoot.getTop() + viewRoot.getBottom()) / 2;

        Drawable d = getResources().getDrawable(R.drawable.wall_2);
        GUIUtils.animateRevealShow(this, mRlContainer, mFab.getWidth() / 2, d,
                cx, cy, new OnRevealAnimationListener() {
                    @Override
                    public void onRevealHide() {

                    }

                    @Override
                    public void onRevealShow() {
                        initViews();
                    }
                });
    }

    private void initViews() {


        new Handler(Looper.getMainLooper()).post(() -> {
            Animation animation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
            animation.setDuration(300);
            mLlContainer.startAnimation(animation);
            // mIvClose.startAnimation(animation);
            mLlContainer.setVisibility(View.VISIBLE);

            //    mIvClose.setVisibility(View.VISIBLE);
        });


    }

    @Override
    public void onBackPressed() {
        GUIUtils.animateRevealHide(this, mRlContainer, R.color.colorAccent, mFab.getWidth() / 2,
                new OnRevealAnimationListener() {
                    @Override
                    public void onRevealHide() {
                        backPressed();
                    }

                    @Override
                    public void onRevealShow() {

                    }
                });
    }


    private void backPressed() {
        super.onBackPressed();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setupExitAnimation() {
        Fade fade = new Fade();
        getWindow().setReturnTransition(fade);
        fade.setDuration(300);
    }


    public void postVacancy() {


        final LoadingDialog loadingDialog = Utilities.showLoadingDialog(SAConstants.POSTING_REQUEST, getSupportFragmentManager());

        try {

            String noOfVacancies = noOfVacanciesSpinner.getSelectedItem().toString();
            String lookingFor = occupantSexSpinner.getSelectedItem().toString();
            String apartmentName = apartmentNameSpinner.getSelectedItem().toString();
            String noOfRooms = noOfRoomsSpinner.getSelectedItem().toString();

            sharedPreferences = getSharedPreferences(SAConstants.sharedPreferenceName, 0);
            byte[] userInformationBytes = Base64.decode(sharedPreferences.getString(SAConstants.USER, ""), Base64.DEFAULT);
            User user = (User) ObjectSerializer.deserialize(userInformationBytes);

            EditText Field = (EditText) pageView
                    .findViewById(R.id.costOfLivingPerMonth);
            String cost = Field.getText().toString();

            EditText notes = (EditText) pageView.findViewById(R.id.notesText);
            String mNotes = notes.getText().toString();


            String url = urlGen.getPostAccUrl(apartmentName, noOfRooms, noOfVacancies, lookingFor, user.getUserId(), cost, mNotes);


            new AccommodationBO(url, loadingDialog);

        } catch (Exception e) {
            ErrorReporting.logReport(e);
        }

    }

    private void setSpinners() {

        // data from local
        apartmentTypeSpinner = (Spinner) pageView.findViewById(R.id.aptTypeSpinner);
        apartmentTypeSpinner.setAdapter(createArrayAdapter(R.array.apartment_type));


        // data from local
        noOfRoomsSpinner = (Spinner) pageView.findViewById(R.id.noOfRoomsSpinner);
        noOfRoomsSpinner.setAdapter(createArrayAdapter(R.array.no_of_rooms));

        // data from local
        noOfVacanciesSpinner = (Spinner) pageView.findViewById(R.id.noOfVacanciesSpinner);
        noOfVacanciesSpinner.setAdapter(createArrayAdapter(R.array.no_of_vacancies));

        // data from local
        occupantSexSpinner = (Spinner) pageView.findViewById(R.id.occcupantSexSpinner);
        occupantSexSpinner.setAdapter(createArrayAdapter(R.array.occcupant_gender));

        // data from server
        apartmentNameSpinner = (Spinner) pageView.findViewById(R.id.aptNameSpinner);
        apartmentNameSpinner.setAdapter(createArrayAdapter(new ArrayList<String>()));

        apartmentTypeSpinner.setOnItemSelectedListener(this);
        apartmentNameSpinner.setOnItemSelectedListener(this);


    }

    public ArrayAdapter<String> createArrayAdapter(List<String> List) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, List);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }

    public ArrayAdapter<CharSequence> createArrayAdapter(int id) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, id, android.R.layout.simple_spinner_item);
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


            if (parent.getId() == R.id.aptTypeSpinner) {

                if (!aptTypeSpinnerVal.equals(apartmentTypeSpinner.getSelectedItem().toString())) {

                    aptTypeSpinnerVal = apartmentTypeSpinner.getSelectedItem().toString();

                    L.m("came to apt type spinner");

                    final ArrayAdapter<String> aptNamesAdapter = (ArrayAdapter<String>) apartmentNameSpinner.getAdapter();
                    aptNamesAdapter.clear();


                    if (reEntryFlag) {
                        mApartmentNames = bundle.getStringArrayList(SAConstants.APARTMENT_NAME);
                        aptNamesAdapter.addAll(mApartmentNames);
                        aptNamesAdapter.notifyDataSetChanged();

                        L.m("spinner position from bundle=" + bundle.getInt(SAConstants.APARTMENT_NAME_POSITION));


                        apartmentNameSpinner.setSelection(bundle.getInt(SAConstants.APARTMENT_NAME_POSITION));
                        reEntryFlag = false;
                    } else {

                        String url = urlGen.getApartmentNamesUrl(apartmentTypeSpinner.getSelectedItem().toString());

                        new AccommodationBO(url, new AccommodationBI() {
                            @Override
                            public void onAccommodationAddsReady(ArrayList<AccommodationAdd> advertisements) {

                            }

                            @Override
                            public void onApartmentNamesReady(ArrayList<String> apartmentNames) {
                                mApartmentNames = apartmentNames;
                                aptNamesAdapter.addAll(apartmentNames);
                                aptNamesAdapter.add(SAConstants.OTHER);
                                aptNamesAdapter.notifyDataSetChanged();
                            }


                        }, SAConstants.APARTMENT_NAMES);

                    }
                }
            } else if (parent.getId() == R.id.aptNameSpinner) {


                if (SAConstants.OTHER.equals(apartmentNameSpinner.getItemAtPosition(position).toString())) {


                    DialogFragment apartmentName = new NewApartmentDialog();
                    apartmentName.show(getSupportFragmentManager(), "");

                }


            }

        } catch (
                Exception e
                )

        {
            ErrorReporting.logReport(e);
        }

    }


    @Override
    public void onNothingSelected(AdapterView<?> arg0) {

    }

    public void aptNameCallback(String apartmentName) {


        String apartmentType = apartmentTypeSpinner.getSelectedItem().toString();

        addNewApartment(apartmentType, apartmentName);

    }

    public void apartmentNameCancelCallback() {

        apartmentNameSpinner.setSelection(0);
    }


    private boolean validateAll() {

        errorQueue.clear();

        if (validateSpinners() && validateCostOfLiving() && validateNotes()) {
            return true;
        }


        String errorContent = "";

        for (String error : errorQueue) {
            errorContent = errorContent + "*" + error + "\n\n";
        }

        Bundle b = new Bundle();
        b.putString(SAConstants.ALERT_TEXT, errorContent);

        AlertDialogL errorDialog = new AlertDialogL();
        errorDialog.setArguments(b);
        errorDialog.show(getSupportFragmentManager(), "");

        return false;


    }


    public void addNewApartment(final String apartmentType, final String apartmentName) {
        try {


            String url = urlGen.getAddNewAptUrl(apartmentName, apartmentType);


            new AccommodationBO(url, new PostAccommodationBI() {
                @Override
                public void onResponse(String response) {

                    ArrayAdapter<String> adapter = (ArrayAdapter<String>) apartmentNameSpinner
                            .getAdapter();
                    adapter.remove(SAConstants.OTHER);
                    adapter.add(apartmentName);
                    adapter.add(SAConstants.OTHER);

                    for (int i = 0; i < adapter.getCount(); i++) {

                        if (adapter.getItem(i).equals(apartmentName)) {
                            apartmentNameSpinner.setSelection(i);
                            break;
                        }
                    }


                }

                @Override
                public void onPostAddResponse(String response, DialogFragment dialogFragment) {

                }
            });


        } catch (Exception e) {
            ErrorReporting.logReport(e);
        }
    }

    public boolean validateNotes() {

        EditText notesText = (EditText) pageView.findViewById(R.id.notesText);
        String notes = notesText.getText().toString();

        String WHITELIST = "[^0-9A-Za-z. ]+";
        Pattern p = Pattern.compile(WHITELIST);
        Matcher matcher = p.matcher(notes);

        if (matcher.find()) {

            errorQueue.add(Alerts.errors.get(8));
            return false;

        }
        return true;

    }

    public boolean validateSpinners() {


        if (noOfVacanciesSpinner.getSelectedItem() == null
                || noOfVacanciesSpinner.getSelectedItem() == null
                || apartmentNameSpinner.getSelectedItem() == null
                || noOfRoomsSpinner.getSelectedItem() == null
                || apartmentTypeSpinner.getSelectedItem() == null) {

            errorQueue.add(Alerts.errors.get(2));
            errorQueue.add(Alerts.errors.get(3));

            return false;

        }

        return true;
    }

    private boolean validateCostOfLiving() {

        EditText Field = (EditText) pageView
                .findViewById(R.id.costOfLivingPerMonth);
        String cost = Field.getText().toString();

        if (cost.equals(null) || cost.equals("")) {

            errorQueue.add(Alerts.errors.get(5));

            return false;


        } else if (Integer.parseInt(cost) <= 0) {

            errorQueue.add(Alerts.errors.get(4) + " " + cost + ".");
            return false;
        }

        return true;

    }


    // response from the accommodation Add Post
    @Override
    public void onResponse(String response) {

        Bundle b = new Bundle();
        b.putString(SAConstants.ALERT_TEXT, SAConstants.SUCCESSFULLY_POSTED);

        AccommodationPosted alert = new AccommodationPosted();
        alert.setArguments(b);
        alert.show(getSupportFragmentManager(), "");

    }


    // callback method from AlertDialogDismiss.java
    // This closes this activity after the accommodation add has been posted
    public void closeActivity() {

        Intent intent = new Intent(this, AccommodationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);


        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        L.m("spinner position=" + apartmentNameSpinner.getSelectedItemPosition());

        outState.putInt(SAConstants.APARTMENT_NAME_POSITION, apartmentNameSpinner.getSelectedItemPosition());
        outState.putStringArrayList(SAConstants.APARTMENT_NAME, mApartmentNames);


    }


}

