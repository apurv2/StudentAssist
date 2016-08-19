package com.apurv.studentassist.accommodation.activities;


import android.app.Application;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;

import com.apurv.studentassist.R;
import com.apurv.studentassist.accommodation.Dialogs.AlertDialogL;
import com.apurv.studentassist.accommodation.Dialogs.LoadingDialog;
import com.apurv.studentassist.accommodation.Interfaces.NotificationsBI;
import com.apurv.studentassist.accommodation.Interfaces.NotificationsInterface;
import com.apurv.studentassist.accommodation.business.rules.AccommodationBO;
import com.apurv.studentassist.accommodation.classes.ApartmentNamesWithType;
import com.apurv.studentassist.accommodation.classes.NotificationSettings;
import com.apurv.studentassist.accommodation.classes.StudentAssistApplication;
import com.apurv.studentassist.accommodation.urlInfo.UrlGenerator;
import com.apurv.studentassist.accommodation.urlInfo.UrlInterface;
import com.apurv.studentassist.internet.DatabaseManager;
import com.apurv.studentassist.internet.NetworkInterface;
import com.apurv.studentassist.util.Alerts;
import com.apurv.studentassist.util.ErrorReporting;
import com.apurv.studentassist.util.L;
import com.apurv.studentassist.util.SAConstants;
import com.apurv.studentassist.util.Utilities;
import com.apurv.studentassist.util.interfaces.LodingDialogInterface;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.google.gson.Gson;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.squareup.leakcanary.LeakCanary;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;

//import com.squareup.leakcanary.LeakCanary;

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

    @Bind(R.id.dormsCheckbox)
    CheckBox mDormsCheckbox;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_settings);
        FacebookSdk.sdkInitialize(StudentAssistApplication.getAppContext());
        ButterKnife.bind(this);

        mToolbar.setTitle(SAConstants.SUBSCRIBE_FOR_NOTIFICATIONS);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        LeakCanary.install((Application) StudentAssistApplication.getAppContext());

        hideViews();
        setSubscriptionSw();
        if (savedInstanceState != null) {

            NotificationSettings settings = savedInstanceState.getParcelable(SAConstants.NOTIFICATION_SETTINGS);

            mApartmentNames = savedInstanceState.getParcelableArrayList(SAConstants.APARTMENT_NAMES);
            setCheckboxes(settings);
            createApartmentNamesCheckbox(mApartmentNames, settings);
            populateSetsAndRadioButons(settings);

        } else {
            getNotificationSettings();
        }

        setFAB();


    }

    private void setSubscriptionSw() {


        mSubscriptionSw.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {


                if (isChecked) {

                    mOnCampusCheckbox.setEnabled(true);
                    mOffCampusCheckbox.setEnabled(true);
                    mDormsCheckbox.setEnabled(true);

                    Utilities.showView(actionButton);


                    for (int i = 0; i < mGenderRadioGroup.getChildCount(); i++) {
                        (mGenderRadioGroup.getChildAt(i)).setEnabled(true);
                    }

                } else {


                    DatabaseManager manager = new DatabaseManager();
                    UrlInterface urlgen = new UrlGenerator();


                    try {


                        UnsuscribeNotifications unsuscribeNotifications = new UnsuscribeNotifications();
                        if (AccessToken.getCurrentAccessToken() != null && !AccessToken.getCurrentAccessToken().isExpired()) {

                            String fbToken = AccessToken.getCurrentAccessToken().getToken();
                            unsuscribeNotifications.setAccess_token(fbToken);
                        }
                        Gson gson = new Gson();
                        String mUnsubscribeJson = gson.toJson(unsuscribeNotifications);

                        L.m("jsonn==" + mUnsubscribeJson);

                        manager.volleyPostRequest(urlgen.unSubscribeNotifications(), new NetworkInterface() {
                            @Override
                            public void onResponseUpdate(String jsonResponse) {

                                L.m("json Response=="+jsonResponse);

                                if (SAConstants.SUCCESS.equals(jsonResponse)) {
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

                            }
                        }, mUnsubscribeJson);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


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

    private class UnsuscribeNotifications {
        String access_token;

        public String getAccess_token() {
            return access_token;
        }

        public void setAccess_token(String access_token) {
            this.access_token = access_token;
        }
    }

    /**
     * checks the APT-Typecheckboxes and radio buttons as per the user's notifications settings and adds onclick listeners
     *
     * @param settings
     */
    private void setCheckboxes(NotificationSettings settings) {


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

    private boolean validate() {

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

            mRequestFlag = false;

            errorQueue.clear();

            if (validate()) {

                L.m("validation error, apt type size==" + mApartmentTypesSet.size());

                String errorContent = "";

                for (String error : errorQueue) {
                    errorContent = errorContent + "*" + error + "\n\n";
                }
                Utilities.showALertDialog(errorContent, getSupportFragmentManager());

            } else {
                NotificationSettings settings = new NotificationSettings();

                if (AccessToken.getCurrentAccessToken() != null && !AccessToken.getCurrentAccessToken().isExpired()) {

                    String fbToken = AccessToken.getCurrentAccessToken().getToken();
                    settings.setAccessToken(fbToken);
                }


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


                SharedPreferences sharedPreferences = StudentAssistApplication.getAppContext().getSharedPreferences(SAConstants.sharedPreferenceName, 0);
                settings.setGcmId(sharedPreferences.getString(SAConstants.GCM_ID, ""));
                settings.setDeviceId(sharedPreferences.getString(SAConstants.INSTANCE_ID, ""));


                Gson gson = new Gson();
                String postParams = gson.toJson(settings);

                UrlInterface urlgen = new UrlGenerator();
                DatabaseManager manager = new DatabaseManager();


                final LoadingDialog loadingDialog = Utilities.showLoadingDialog(SAConstants.POSTING_REQUEST, getSupportFragmentManager());


                manager.volleyPostRequestWithLoadingDialog(urlgen.getSubscribeNotificationsUrl(), loadingDialog, postParams);
            }

        } catch (Exception e) {

            e.printStackTrace();

        }
    }


    private void setFAB() {

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


    }


    /**
     * Callback method after the user interacts with the Permissions popup
     *
     * @param requestCode  contains the request code for the permission
     * @param permissions  array that contains all permissions
     * @param grantResults array containing code for granted and not granted.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case 0: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    subscribeNotifications();

                } else {

                    //flag to track the user permission for GPS
                    mAskForPhoneStatePermission = true;
                }
                return;
            }


        }


    }

    private void hideViews() {
        Utilities.hideView(findViewById(R.id.onCampus));
        Utilities.hideView(findViewById(R.id.offCampus));
        Utilities.hideView(findViewById(R.id.dorms));


    }

    /**
     * 1. created apartmentNames checkboxes
     * 2. Checks the checkboxes as per user's notification settings.
     *
     * @param apartmentNames
     * @param settings
     */
    private void createApartmentNamesCheckbox(List<ApartmentNamesWithType> apartmentNames, NotificationSettings settings) {


        ContextThemeWrapper mCheckboxThemeContext = new ContextThemeWrapper(getApplicationContext(),
                R.style.MyCheckBox);
        LinearLayout onCampusLayout = (LinearLayout) findViewById(R.id.onCampus);
        LinearLayout offCampusLayout = (LinearLayout) findViewById(R.id.offCampus);
        LinearLayout dormsLayout = (LinearLayout) findViewById(R.id.dorms);

        CheckBox mApartmentNamesCheckbox;
        for (ApartmentNamesWithType apartmentName : apartmentNames) {


            mApartmentNamesCheckbox = new CheckBox(mCheckboxThemeContext);
            mApartmentNamesCheckbox.setText(apartmentName.getApartmentName());

            if (settings.getApartmentName().contains(apartmentName.getApartmentName())) {
                mApartmentNamesCheckbox.setChecked(true);

            }


            mApartmentNamesCheckbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View mCheckboxView) {

                    CheckBox checkBox = (CheckBox) mCheckboxView;
                    if (checkBox.isChecked()) {
                        mApartmentNamesSet.add(String.valueOf(checkBox.getText()));

                    } else {
                        mApartmentNamesSet.remove(String.valueOf(checkBox.getText()));
                    }

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


    /**
     * Fetches apartmentNames and calls createApartmentNamesCheckbox method.
     *
     * @param settings
     */
    private void getApartmentNames(final NotificationSettings settings) {

        AccommodationBO bo = new AccommodationBO();
        String url = "";
        try {
            url = urlGen.getApartmentNamesWithTypeUrl();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        bo.getApartmentNamesWithType(url, new NotificationsBI() {
            @Override
            public void onApartmentNamesReady(List<ApartmentNamesWithType> apartmentNames) {

                mApartmentNames = apartmentNames;
                createApartmentNamesCheckbox(apartmentNames, settings);
            }
        });
    }

    private void getNotificationSettings() {

        AccommodationBO accommodationBO = new AccommodationBO();
        UrlInterface urlInterface = new UrlGenerator();

        if (AccessToken.getCurrentAccessToken() != null && !AccessToken.getCurrentAccessToken().isExpired()) {

            try {
                String fbToken = AccessToken.getCurrentAccessToken().getToken();
                String url = urlInterface.getNotificationSettingsUrl(fbToken);
                accommodationBO.getNotificationSettings(url, new NotificationsInterface() {
                    @Override
                    public void onResponse(NotificationSettings settings) {

                        setCheckboxes(settings);
                        getApartmentNames(settings);
                        populateSetsAndRadioButons(settings);


                    }
                });

            } catch (Exception e) {
                ErrorReporting.logReport(e);
            }


        }


    }

    /**
     * populates sets with user's preferences and Radio buttons.
     *
     * @param settings
     */
    private void populateSetsAndRadioButons(NotificationSettings settings) {

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
     * Activity life cycle method that called when the activity resumes.
     */
    @Override
    public void onResponse(String response) {

        if (SAConstants.SUCCESS.equals(response)) {

            Bundle b = new Bundle();
            b.putString(SAConstants.ALERT_TEXT, SAConstants.SUCCESS);
            AlertDialogL errorDialog = new AlertDialogL();
            errorDialog.setArguments(b);
            errorDialog.show(getSupportFragmentManager(), "");

        }


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
