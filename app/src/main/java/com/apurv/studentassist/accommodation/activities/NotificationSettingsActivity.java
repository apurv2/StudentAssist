package com.apurv.studentassist.accommodation.activities;


import android.animation.ValueAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.Request;
import com.apurv.studentassist.R;
import com.apurv.studentassist.accommodation.Dialogs.AlertDialogL;
import com.apurv.studentassist.accommodation.Dialogs.LoadingDialog;
import com.apurv.studentassist.accommodation.Dialogs.SelectUniversityDialog;
import com.apurv.studentassist.accommodation.classes.ApartmentNamesWithType;
import com.apurv.studentassist.accommodation.classes.NotificationSettings;
import com.apurv.studentassist.accommodation.classes.RApartmentNamesInUnivs;
import com.apurv.studentassist.accommodation.classes.RApartmentNamesWithType;
import com.apurv.studentassist.accommodation.classes.StudentAssistApplication;
import com.apurv.studentassist.accommodation.urlInfo.UrlGenerator;
import com.apurv.studentassist.accommodation.urlInfo.UrlInterface;
import com.apurv.studentassist.internet.NetworkInterface;
import com.apurv.studentassist.internet.StudentAssistBO;
import com.apurv.studentassist.util.Alerts;
import com.apurv.studentassist.util.ErrorReporting;
import com.apurv.studentassist.util.L;
import com.apurv.studentassist.util.SAConstants;
import com.apurv.studentassist.util.Utilities;
import com.apurv.studentassist.util.interfaces.LodingDialogInterface;
import com.facebook.FacebookSdk;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.apurv.studentassist.util.Utilities.hideFabLayout;
import static com.apurv.studentassist.util.Utilities.rotateAnticlockwise;
import static com.apurv.studentassist.util.Utilities.rotateClockwise;
import static com.apurv.studentassist.util.Utilities.showFabLayout;


public class NotificationSettingsActivity extends AppCompatActivity implements LodingDialogInterface {


    UrlInterface urlGen = new UrlGenerator();
    final Set<String> mApartmentNamesSet = new LinkedHashSet<String>();
    final Set<String> mApartmentTypesSet = new LinkedHashSet<String>();
    String mGender = "";
    boolean mAskForPhoneStatePermission = false;
    boolean mRequestFlag = false;
    List<ApartmentNamesWithType> mApartmentNames;
    List<String> errorQueue = new ArrayList<String>();
    FloatingActionButton actionButton;
    boolean fabOpen;

    @Bind(R.id.genderRadioGroup)
    RadioGroup mGenderRadioGroup;

    @Bind(R.id.notificationToolbar)
    Toolbar mToolbar;


    @Bind(R.id.subscriptionSw)
    Switch mSubscriptionSw;


    @Bind(R.id.onCampusCheckbox)
    CheckBox mOnCampusCheckbox;


    @Bind(R.id.offCampusCheckbox)
    CheckBox mOffCampusCheckbox;

    @Bind(R.id.fabPlus)
    FloatingActionButton fabPlus;

    @Bind(R.id.fabChangeUniversity)
    LinearLayout fabChangeUniversity;

    @Bind(R.id.fabSubscribe)
    LinearLayout fabSubscribe;

    @Bind(R.id.dormsCheckbox)
    CheckBox mDormsCheckbox;


    ValueAnimator anim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_settings);
        FacebookSdk.sdkInitialize(StudentAssistApplication.getAppContext());
        ButterKnife.bind(this);

        mToolbar.setTitle(SAConstants.SUBSCRIBE_FOR_NOTIFICATIONS);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //LeakCanary.install((Application) StudentAssistApplication.getAppContext());

        //Navigation Icon
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NotificationSettingsActivity.super.onBackPressed();
            }
        });


        setFAB();
        hideViews();
        setSubscriptionSw();
        if (savedInstanceState != null) {

            NotificationSettings settings = savedInstanceState.getParcelable(SAConstants.NOTIFICATION_SETTINGS);

            mApartmentNames = savedInstanceState.getParcelableArrayList(SAConstants.APARTMENT_NAMES);
            setCheckboxes(settings);
            createApartmentNamesCheckbox(settings);
            populateSetsAndRadioButtons(settings);

        } else {
            getNotificationSettings();
        }

    }

    private void setSubscriptionSw() {


        mSubscriptionSw.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if (isNetworkAvailable()) {

                    if (isChecked) {

                        mOnCampusCheckbox.setEnabled(true);
                        mOffCampusCheckbox.setEnabled(true);
                        mDormsCheckbox.setEnabled(true);

                        Utilities.showView(actionButton);


                        for (int i = 0; i < mGenderRadioGroup.getChildCount(); i++) {
                            (mGenderRadioGroup.getChildAt(i)).setEnabled(true);
                        }

                    } else {


                        StudentAssistBO manager = new StudentAssistBO();
                        UrlInterface urlgen = new UrlGenerator();


                        try {

                            //sitiation when the user does not have any subscription settings and this function is being called by the framework
                            // when we are setting the subscription switch to false. ( We want to set it to false because of business need)
                            if (mGender != null && mGender != "") {

                                manager.volleyRequest(urlgen.unSubscribeNotifications(), new NetworkInterface() {
                                    @Override
                                    public void onResponseUpdate(String jsonResponse) {

                                        try {

                                            JSONObject jObject = new JSONObject(jsonResponse);
                                            String responseString = "";

                                            if (jObject.has(SAConstants.RESPONSE)) {
                                                responseString = jObject.getString(SAConstants.RESPONSE);
                                            }

                                            if (SAConstants.SUCCESS.equals(responseString)) {
                                                Utilities.hideView(actionButton);

                                                unCheckAptNamesCheckboxes((ViewGroup) findViewById(R.id.onCampus));
                                                unCheckAptNamesCheckboxes((ViewGroup) findViewById(R.id.offCampus));
                                                unCheckAptNamesCheckboxes((ViewGroup) findViewById(R.id.dorms));

                                                Utilities.hideView(findViewById(R.id.onCampus));
                                                Utilities.hideView(findViewById(R.id.offCampus));
                                                Utilities.hideView(findViewById(R.id.dorms));

                                                mOnCampusCheckbox.setChecked(false);
                                                mOffCampusCheckbox.setChecked(false);
                                                mDormsCheckbox.setChecked(false);

                                                mGenderRadioGroup.clearCheck();

                                                mOnCampusCheckbox.setEnabled(false);
                                                mOffCampusCheckbox.setEnabled(false);
                                                mDormsCheckbox.setEnabled(false);

                                                for (int i = 0; i < mGenderRadioGroup.getChildCount(); i++) {
                                                    (mGenderRadioGroup.getChildAt(i)).setEnabled(false);
                                                }


                                            } else {
                                                Bundle b = new Bundle();
                                                b.putString(SAConstants.ALERT_TEXT, SAConstants.FAILED_TO_UNSUBSCRIBE);
                                                AlertDialogL errorDialog = new AlertDialogL();
                                                errorDialog.setArguments(b);
                                                errorDialog.show(getSupportFragmentManager(), "");

                                            }
                                        } catch (Exception e) {

                                            ErrorReporting.logReport(e);
                                            Bundle b = new Bundle();
                                            b.putString(SAConstants.ALERT_TEXT, SAConstants.FAILED_TO_UNSUBSCRIBE);
                                            AlertDialogL errorDialog = new AlertDialogL();
                                            errorDialog.setArguments(b);
                                            errorDialog.show(getSupportFragmentManager(), "");

                                        }
                                    }
                                }, "", Request.Method.DELETE);

                            }

                        } catch (Exception e) {
                            ErrorReporting.logReport(e);
                        }


                    }
                } else {
                    Toast.makeText(getApplicationContext(), Alerts.errors.get(2), Toast.LENGTH_LONG).show();
                    mSubscriptionSw.setChecked(false);

                }


            }
        });


    }

    private void unCheckAptNamesCheckboxes(ViewGroup mViewGroup) {

        if (mViewGroup != null) {
            for (int index = 0; index < (mViewGroup).getChildCount(); ++index) {
                View nextChild = (mViewGroup).getChildAt(index);

                if (nextChild instanceof CheckBox) {
                    CheckBox mCheckbox = (CheckBox) nextChild;
                    mApartmentNamesSet.remove(mCheckbox.getText());
                    mCheckbox.setChecked(false);
                }
            }

        }
    }

    /**
     * checks the APT-Typecheckboxes and radio buttons as per the user's notifications settings and adds onclick listeners
     *
     * @param settings
     */
    private void setCheckboxes(NotificationSettings settings) {

        if (settings != null && settings.getApartmentType() != null) {
            for (String apartmentType : settings.getApartmentType()) {

                switch (apartmentType) {
                    case SAConstants.ON:
                        Utilities.showView(findViewById(R.id.onCampus));
                        mOnCampusCheckbox.setChecked(true);
                        break;
                    case SAConstants.OFF:
                        Utilities.showView(findViewById(R.id.offCampus));
                        mOffCampusCheckbox.setChecked(true);
                        break;
                    case SAConstants.DORMS:
                        mDormsCheckbox.setChecked(true);
                        Utilities.showView(findViewById(R.id.dorms));
                        break;
                }
            }
        }
        View.OnClickListener mCheckboxListener = new View.OnClickListener() {
            @Override
            public void onClick(View mNotificatinoSettingItem) {

                String aptTypeCheckboxValue = "";

                CheckBox checkBox = (CheckBox) mNotificatinoSettingItem;
                aptTypeCheckboxValue = checkBox.getText() + "";

                if (checkBox.isChecked()) {


                    mApartmentTypesSet.add(UrlGenerator.getApartmentTypeCodeMap().get(aptTypeCheckboxValue));


                    switch (UrlGenerator.getApartmentTypeCodeMap().get(aptTypeCheckboxValue)) {
                        case SAConstants.ON:

                            Utilities.showView(findViewById(R.id.onCampus));
                            break;
                        case SAConstants.OFF:
                            Utilities.showView(findViewById(R.id.offCampus));
                            break;
                        case SAConstants.DORMS:
                            Utilities.showView(findViewById(R.id.dorms));
                            break;
                    }


                } else {

                    ViewGroup mViewGroup = null;

                    mApartmentTypesSet.remove(UrlGenerator.getApartmentTypeCodeMap().get(aptTypeCheckboxValue));
                    switch (UrlGenerator.getApartmentTypeCodeMap().get(aptTypeCheckboxValue)) {
                        case SAConstants.ON:

                            mViewGroup = (ViewGroup) findViewById(R.id.onCampus);
                            Utilities.hideView(mViewGroup);
                            unCheckAptNamesCheckboxes(mViewGroup);
                            break;
                        case SAConstants.OFF:
                            mViewGroup = (ViewGroup) findViewById(R.id.offCampus);
                            Utilities.hideView(mViewGroup);
                            unCheckAptNamesCheckboxes(mViewGroup);
                            break;
                        case SAConstants.DORMS:
                            mViewGroup = (ViewGroup) findViewById(R.id.dorms);
                            Utilities.hideView(mViewGroup);
                            unCheckAptNamesCheckboxes(mViewGroup);
                            break;
                    }


                }

            }
        };

        mOffCampusCheckbox.setOnClickListener(mCheckboxListener);
        mOnCampusCheckbox.setOnClickListener(mCheckboxListener);
        mDormsCheckbox.setOnClickListener(mCheckboxListener);

    }

    public void changeUniversity(NotificationSettings settings) {
        populateNotifications(settings);
    }


    private boolean ifValidationFails() {

        boolean mValidateSw = false;
        String errors = "";
        if (mApartmentTypesSet.size() == 0) {
            errorQueue.add(Alerts.errors.get(14));
            mValidateSw = true;
        }


        if (mGenderRadioGroup.getCheckedRadioButtonId() == -1) {
            errorQueue.add(Alerts.errors.get(13));
            mValidateSw = true;
        }


        return mValidateSw;
    }

    private void subscribeNotifications() {
        try {

            L.m("subscription requrested");
            errorQueue.clear();

            if (ifValidationFails()) {

                L.m("validation error, apt type size==" + mApartmentTypesSet.size());

                String errorContent = "";

                for (String error : errorQueue) {
                    errorContent = errorContent + "*" + error + "\n\n";
                }
                Utilities.showALertDialog(errorContent, getSupportFragmentManager());

            } else {
                NotificationSettings settings = new NotificationSettings();
                for (String mApartmentName : mApartmentNamesSet) {
                    settings.getApartmentName().add(mApartmentName);
                }

                int selectedId = mGenderRadioGroup.getCheckedRadioButtonId();
                if (selectedId != -1) {
                    RadioButton genderRadioButton = (RadioButton) findViewById(selectedId);
                    mGender = String.valueOf(genderRadioButton.getText());
                }

                settings.setGender(mGender);

                for (String mApartmentType : mApartmentTypesSet) {
                    settings.getApartmentType().add(mApartmentType);
                    L.m("apartment type==" + mApartmentType);

                }


                SharedPreferences sharedPreferences = StudentAssistApplication.getAppContext().getSharedPreferences(SAConstants.SHARED_PREFERENCE_NAME, 0);
                settings.setGcmId(sharedPreferences.getString(SAConstants.GCM_ID, ""));
                settings.setInstanceId(sharedPreferences.getString(SAConstants.INSTANCE_ID, ""));


                Gson gson = new Gson();
                String postParams = gson.toJson(settings);

                UrlInterface urlgen = new UrlGenerator();
                StudentAssistBO manager = new StudentAssistBO();


                final LoadingDialog loadingDialog = Utilities.showLoadingDialog(SAConstants.POSTING_REQUEST, getSupportFragmentManager());


                manager.volleyRequestWithLoadingDialog(urlgen.getSubscribeNotificationsUrl(), loadingDialog, postParams, Request.Method.PUT);
            }

        } catch (Exception e) {

            e.printStackTrace();

        }
    }


    @OnClick(R.id.fabChangeUniversity)
    public void changeUniversity(View view) {
        //TODO
    }

    @OnClick(R.id.fabSubscribe)
    public void subscribeNotifications(View view) {
        subscribeNotifications();
    }


    private void setFAB() {

        // Utilities.revealShow(fabPlus);
        fabPlus.setOnClickListener(v -> {
            if (fabOpen) {

                Utilities.rotateAnimation(v, rotateAnticlockwise);
                fabChangeUniversity.startAnimation(hideFabLayout);
                fabSubscribe.startAnimation(hideFabLayout);
                fabOpen = false;
            } else {
                Utilities.rotateAnimation(v, rotateClockwise);
                fabChangeUniversity.startAnimation(showFabLayout);
                fabSubscribe.startAnimation(showFabLayout);
                fabOpen = true;
            }
        });
/*
        ImageView icon = new ImageView(this); // Create an icon
        icon.setImageResource(R.drawable.ic_subscribe);
        actionButton = new FloatingActionButton.Builder(this)
                .setContentView(icon)
                .build();

        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mRequestFlag = true;
                subscribeNotifications();

            }
        });
*/


    }

    private void hideViews() {
        Utilities.hideView(findViewById(R.id.onCampus));
        Utilities.hideView(findViewById(R.id.offCampus));
        Utilities.hideView(findViewById(R.id.dorms));


    }

    /**
     * 1. created apartmentNames checkboxes
     * 2. Checks(ticks) the checkboxes as per user's notification settings.
     *
     * @param settings
     */
    public void createApartmentNamesCheckbox(NotificationSettings settings) {


        ContextThemeWrapper mCheckboxThemeContext = new ContextThemeWrapper(getApplicationContext(),
                R.style.MyCheckBox);
        LinearLayout onCampusLayout = (LinearLayout) findViewById(R.id.onCampus);
        LinearLayout offCampusLayout = (LinearLayout) findViewById(R.id.offCampus);
        LinearLayout dormsLayout = (LinearLayout) findViewById(R.id.dorms);

        CheckBox mApartmentNamesCheckbox;
        for (RApartmentNamesInUnivs apartmentNamesInUniv : settings.getApartmentNames()) {

            if (apartmentNamesInUniv.getUniversityId() == settings.getUniversityId()) {

                List<RApartmentNamesWithType> apartmentNamesWithType = apartmentNamesInUniv.getApartmentNames();

                for (RApartmentNamesWithType apartmentName : apartmentNamesWithType) {


                    mApartmentNamesCheckbox = new CheckBox(mCheckboxThemeContext);
                    mApartmentNamesCheckbox.setText(apartmentName.getApartmentName());

                    if (settings.getApartmentName() != null && settings.getApartmentName().contains(apartmentName.getApartmentName())) {
                        mApartmentNamesCheckbox.setChecked(true);

                    }
                    mApartmentNamesCheckbox.setOnClickListener(mCheckboxView -> {
                        CheckBox checkBox = (CheckBox) mCheckboxView;
                        if (checkBox.isChecked()) {
                            mApartmentNamesSet.add(String.valueOf(checkBox.getText()));

                        } else {
                            mApartmentNamesSet.remove(String.valueOf(checkBox.getText()));
                        }

                    });


                    switch (apartmentName.getApartmentType()) {
                        case SAConstants.ON:
                            onCampusLayout.addView(mApartmentNamesCheckbox);
                            break;

                        case SAConstants.OFF:
                            offCampusLayout.addView(mApartmentNamesCheckbox);
                            break;
                        case SAConstants.DORMS:
                            dormsLayout.addView(mApartmentNamesCheckbox);
                            break;

                    }


                }
            }
        }
    }


    /**
     * Fetches apartmentNames and calls createApartmentNamesCheckbox method.
     *
     * @param settings
     */
    private void getApartmentNames(final NotificationSettings settings) {
        createApartmentNamesCheckbox(settings);
    }

    private void getNotificationSettings() {

        UrlInterface urlInterface = new UrlGenerator();
        try {
            String url = urlInterface.getNotificationSettingsUrl();
            final LoadingDialog loadingDialog = Utilities.showLoadingDialog(SAConstants.GETTING_NOTIFICATION_SETTINGS, getSupportFragmentManager());

            StudentAssistBO studentAssistBO = new StudentAssistBO();
            studentAssistBO.volleyRequestWithLoadingDialog(url, loadingDialog, null, Request.Method.GET);

        } catch (Exception e) {
            ErrorReporting.logReport(e);
        }


    }

    private void populateNotifications(NotificationSettings settings) {

        if (settings.getUniversityId() == -1) {

            Bundle b = new Bundle();
            b.putParcelable(SAConstants.NOTIFICATION_SETTINGS, settings);
            SelectUniversityDialog selectUniversityDialog = new SelectUniversityDialog();
            selectUniversityDialog.setArguments(b);
            selectUniversityDialog.show(getSupportFragmentManager(), "");

        } else {
            setCheckboxes(settings);
            getApartmentNames(settings);
            populateSetsAndRadioButtons(settings);
        }


    }

    /**
     * called when a new university is selected from SelectUniversity Dialog box
     */
    public void createNewNotificationSettings(NotificationSettings settings) {
        createApartmentNamesCheckbox(settings);
        setCheckboxes(settings);
    }


    /**
     * populates sets with user's preferences and Radio buttons.
     *
     * @param settings
     */
    private void populateSetsAndRadioButtons(NotificationSettings settings) {

        mApartmentTypesSet.clear();
        mApartmentNamesSet.clear();
        mGender = settings.getGender();
        mApartmentTypesSet.addAll(settings.getApartmentType());
        mApartmentNamesSet.addAll(settings.getApartmentName());

        if (mGender == null) {


            mSubscriptionSw.setChecked(false);
            Utilities.hideView(actionButton);

            mOnCampusCheckbox.setEnabled(false);
            mOffCampusCheckbox.setEnabled(false);
            mDormsCheckbox.setEnabled(false);

            for (int i = 0; i < mGenderRadioGroup.getChildCount(); i++) {
                (mGenderRadioGroup.getChildAt(i)).setEnabled(false);
            }


        } else {
            mSubscriptionSw.setChecked(true);

            switch (mGender) {
                case SAConstants.MALE:
                    mGenderRadioGroup.check(R.id.maleRadio);
                    break;

                case SAConstants.FEMALE:
                    mGenderRadioGroup.check(R.id.femaleRadio);
                    break;

                case SAConstants.DOESNT_MATTER:
                    mGenderRadioGroup.check(R.id.doesntMatterRadio);
                    break;


            }


        }
    }

    /**
     * This method is used as a callback for 2 methods:
     * 1. when subscriptions are posted
     * 2. when subscriptions are received.
     *
     * @param response
     */
    @Override
    public void onResponse(String response) {
        try {

            JSONObject jObject = new JSONObject(response);
            String responseString = "";

            if (jObject.has(SAConstants.RESPONSE)) {
                responseString = jObject.getString(SAConstants.RESPONSE);
            }


            if (SAConstants.SUCCESS.equals(responseString)) {

                Bundle b = new Bundle();
                b.putString(SAConstants.ALERT_TEXT, SAConstants.SUCCESS);
                AlertDialogL alertDialogL = new AlertDialogL();
                alertDialogL.setArguments(b);
                alertDialogL.show(getSupportFragmentManager(), "");

            } else if (!SAConstants.FAILURE.equals(response)) {
                Gson gson = new Gson();
                NotificationSettings settings = gson.fromJson(response, NotificationSettings.class);
                populateNotifications(settings);
            }
        } catch (Exception e) {

            populateNotifications(new NotificationSettings());
            e.printStackTrace();

        }

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        NotificationSettings settings = new NotificationSettings();
        settings.getApartmentName().addAll(mApartmentNamesSet);
        settings.getApartmentType().addAll(mApartmentTypesSet);

        int selectedId = mGenderRadioGroup.getCheckedRadioButtonId();
        if (selectedId != -1) {
            RadioButton genderRadioButton = (RadioButton) findViewById(selectedId);
            mGender = String.valueOf(genderRadioButton.getText());
        }

        settings.setGender(mGender);
        outState.putParcelable(SAConstants.NOTIFICATION_SETTINGS, settings);
        outState.putParcelableArrayList(SAConstants.APARTMENT_NAMES, (ArrayList<ApartmentNamesWithType>) mApartmentNames);


    }

}
