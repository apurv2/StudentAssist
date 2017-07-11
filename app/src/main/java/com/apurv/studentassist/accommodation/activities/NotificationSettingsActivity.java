package com.apurv.studentassist.accommodation.activities;


import android.animation.ValueAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextThemeWrapper;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.apurv.studentassist.R;
import com.apurv.studentassist.accommodation.Dialogs.AlertDialogL;
import com.apurv.studentassist.accommodation.Dialogs.ChangeUniversityPromptDialog;
import com.apurv.studentassist.accommodation.Dialogs.LoadingDialog;
import com.apurv.studentassist.accommodation.Dialogs.SaveSettingsPrompt;
import com.apurv.studentassist.accommodation.Dialogs.SelectUniversityDialog;
import com.apurv.studentassist.accommodation.Dialogs.UnsubsribePromptDialog;
import com.apurv.studentassist.accommodation.classes.ApartmentNamesWithType;
import com.apurv.studentassist.accommodation.classes.NotificationSettings;
import com.apurv.studentassist.accommodation.classes.RApartmentNamesInUnivs;
import com.apurv.studentassist.accommodation.classes.RApartmentNamesWithType;
import com.apurv.studentassist.accommodation.classes.StudentAssistApplication;
import com.apurv.studentassist.accommodation.urlInfo.UrlGenerator;
import com.apurv.studentassist.accommodation.urlInfo.UrlInterface;
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

import static com.apurv.studentassist.R.id.genderRadioGroup;
import static com.apurv.studentassist.util.Utilities.rotateAnticlockwise;
import static com.apurv.studentassist.util.Utilities.rotateClockwise;
import static com.apurv.studentassist.util.Utilities.showFabLayout;


public class NotificationSettingsActivity extends AppCompatActivity implements LodingDialogInterface {


    UrlInterface urlGen = new UrlGenerator();
    final Set<String> mApartmentNamesSet = new LinkedHashSet<String>();
    final Set<String> mApartmentTypesSet = new LinkedHashSet<String>();
    String mGender = "";
    List<ApartmentNamesWithType> mApartmentNames;
    List<String> errorQueue = new ArrayList<String>();
    NotificationSettings settings;
    boolean serverHasSettings;
    boolean fabOpen;

    @Bind(genderRadioGroup)
    RadioGroup mGenderRadioGroup;

    @Bind(R.id.notificationToolbar)
    Toolbar mToolbar;

    @Bind(R.id.onCampusCheckbox)
    CheckBox mOnCampusCheckbox;


    @Bind(R.id.offCampusCheckbox)
    CheckBox mOffCampusCheckbox;

    @Bind(R.id.fabPlus)
    FloatingActionButton fabPlus;

    @Bind(R.id.fabChangeUniversity)
    LinearLayout fabChangeUniversity;

    @Bind(R.id.unsubscribe)
    LinearLayout fabUnSubscribe;

    @Bind(R.id.fabSubscribe)
    LinearLayout fabSubscribe;

    @Bind(R.id.dormsCheckbox)
    CheckBox mDormsCheckbox;


    @Bind(R.id.onCampusCheckboxes)
    LinearLayout mOnCampusCheckboxes;

    @Bind(R.id.offCampusCheckboxes)
    LinearLayout mOffCampusCheckboxes;

    @Bind(R.id.dormsCheckboxes)
    LinearLayout mDormsCheckboxes;


    @Bind(R.id.rootNotificationSettingsView)
    LinearLayout rootNotificationSettingsView;

    @Bind(R.id.selectUniversity)
    TextView selectUniversityTv;

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
                onBackPressed();
            }
        });


        setFAB();
        hideViews();
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

    public void openNavTray() {
        fabPlus.startAnimation(rotateClockwise);
        fabChangeUniversity.startAnimation(showFabLayout);
        fabSubscribe.startAnimation(showFabLayout);
        Utilities.showView(fabChangeUniversity);
        Utilities.showView(fabSubscribe);

        fabOpen = true;

        if (serverHasSettings) {
            fabUnSubscribe.startAnimation(showFabLayout);
            Utilities.showView(fabUnSubscribe);
        }

    }

    public void closeActivity() {
        super.onBackPressed();
    }

    private void closeNavTray() {


        Animation hideFabLayout;
        hideFabLayout = AnimationUtils.loadAnimation(this, R.anim.hide_layout);

        fabPlus.startAnimation(rotateAnticlockwise);
        fabChangeUniversity.startAnimation(hideFabLayout);
        fabSubscribe.startAnimation(hideFabLayout);

        if (serverHasSettings) {
            fabUnSubscribe.startAnimation(hideFabLayout);
        }
        hideFabLayout.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Utilities.hideView(fabChangeUniversity);
                Utilities.hideView(fabSubscribe);
                if (serverHasSettings) {
                    Utilities.hideView(fabUnSubscribe);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


        fabOpen = false;


    }

    /**
     * Unsubscribes notifications setings
     * Note: This method does not remove the Apartment names checkboxes after successful removal of
     * subscription -> It will automatically be removed when user selects a new university in the changeUniversity() method.
     */
    public void unSubscribeNotifications() {

        StudentAssistBO manager = new StudentAssistBO();
        UrlInterface urlgen = new UrlGenerator();
        try {

            manager.volleyRequest(urlgen.unSubscribeNotifications(), jsonResponse -> {

                try {

                    JSONObject jObject = new JSONObject(jsonResponse);
                    String responseString = "";

                    if (jObject.has(SAConstants.RESPONSE)) {
                        responseString = jObject.getString(SAConstants.RESPONSE);
                    }

                    if (SAConstants.SUCCESS.equals(responseString)) {

                        mToolbar.setTitle(SAConstants.SUBSCRIBE_FOR_NOTIFICATIONS);
                        mApartmentNamesSet.clear();
                        mApartmentTypesSet.clear();
                        mGender = null;

                        serverHasSettings = false;
                        this.settings.setUniversityId(-1);
                        this.settings.setApartmentName(null);
                        this.settings.setApartmentType(null);
                        this.settings.setGender(null);


                        selectUniversityTv.setText(SAConstants.SELECT_UNIVERSITY);

                        Utilities.hideView(findViewById(R.id.onCampus));
                        Utilities.hideView(findViewById(R.id.offCampus));
                        Utilities.hideView(findViewById(R.id.dorms));

                        //clearing checxboxes
                        mOnCampusCheckbox.setChecked(false);
                        mOffCampusCheckbox.setChecked(false);
                        mDormsCheckbox.setChecked(false);
                        mGenderRadioGroup.clearCheck();

                        Utilities.hideView(findViewById(R.id.genderRadioGroup));
                        Utilities.hideView(findViewById(R.id.aptTypesChxBx));


                        openNavTray();
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
            }, "", Request.Method.DELETE);


        } catch (Exception e) {
            ErrorReporting.logReport(e);
        }
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
                            unCheckAptNamesCheckboxes(mOnCampusCheckboxes);
                            break;
                        case SAConstants.OFF:
                            mViewGroup = (ViewGroup) findViewById(R.id.offCampus);
                            Utilities.hideView(mViewGroup);
                            unCheckAptNamesCheckboxes(mOffCampusCheckboxes);
                            break;
                        case SAConstants.DORMS:
                            mViewGroup = (ViewGroup) findViewById(R.id.dorms);
                            Utilities.hideView(mViewGroup);
                            unCheckAptNamesCheckboxes(mDormsCheckboxes);
                            break;
                    }
                }
            }
        };
        mOffCampusCheckbox.setOnClickListener(mCheckboxListener);
        mOnCampusCheckbox.setOnClickListener(mCheckboxListener);
        mDormsCheckbox.setOnClickListener(mCheckboxListener);
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

    public void subscribeNotifications() {
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
                settings.setUniversityId(this.settings.getUniversityId());

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
                settings.setApartmentNames(this.settings.getApartmentNames());
                this.settings = settings;

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
    public void changeUniversityConfirmation(View view) {

        //check if a University has already  been selected. If so show change university alert
        if (serverHasSettings || settings.getUniversityId() != -1) {
            Bundle b = new Bundle();
            b.putString(SAConstants.ALERT_TEXT, SAConstants.CHANGE_UNIVERSITY_PROMPT);
            ChangeUniversityPromptDialog changeUniversityPromptDialog = new ChangeUniversityPromptDialog();
            changeUniversityPromptDialog.setArguments(b);
            changeUniversityPromptDialog.show(getSupportFragmentManager(), "");
        } else {
            openSelectUniversityDialog(settings);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        openNavTray();
    }

    @OnClick(R.id.unsubscribe)
    public void unsubscribe(View view) {
        Bundle b = new Bundle();
        b.putString(SAConstants.ALERT_TEXT, SAConstants.SELECT_UNIVERSITY_PROMPT);
        UnsubsribePromptDialog unsubsribePromptDialog = new UnsubsribePromptDialog();
        unsubsribePromptDialog.setArguments(b);
        unsubsribePromptDialog.show(getSupportFragmentManager(), "");

    }


    public void changeUniversity() {
        openSelectUniversityDialog(settings);
    }

    @OnClick(R.id.fabSubscribe)
    public void subscribeNotifications(View view) {
        subscribeNotifications();
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (fabOpen) {
                Rect outRect = new Rect();
                fabPlus.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    closeNavTray();
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    private void setFAB() {

        fabPlus.setOnClickListener(v -> {
            if (fabOpen) {
                //close Nav tray
                closeNavTray();
            } else {
                // open tray
                openNavTray();
            }
        });
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
        LinearLayout onCampusLayout = (LinearLayout) findViewById(R.id.onCampusCheckboxes);
        LinearLayout offCampusLayout = (LinearLayout) findViewById(R.id.offCampusCheckboxes);
        LinearLayout dormsLayout = (LinearLayout) findViewById(R.id.dormsCheckboxes);

        CheckBox mApartmentNamesCheckbox;
        for (RApartmentNamesInUnivs apartmentNamesInUniv : settings.getApartmentNames()) {

            if (apartmentNamesInUniv.getUniversityId() == settings.getUniversityId()) {

                mToolbar.setTitle(apartmentNamesInUniv.getUniversityName());

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

    /**
     * @param settings called at page starting after getting university settings from server
     */
    private void populateNotifications(NotificationSettings settings) {

        if (settings.getUniversityId() == -1) {

            openSelectUniversityDialog(settings);
            Utilities.hideView(findViewById(R.id.genderRadioGroup));
            Utilities.hideView(findViewById(R.id.aptTypesChxBx));
        } else {
            // logic to add settings to the sets
            mApartmentTypesSet.addAll(settings.getApartmentType());
            mApartmentNamesSet.addAll(settings.getApartmentName());

            serverHasSettings = true;
            selectUniversityTv.setText(SAConstants.CHANGE_UNIVERSITY);
            setCheckboxes(settings);
            getApartmentNames(settings);
            populateSetsAndRadioButtons(settings);
        }

        Utilities.revealShow(fabPlus);

        /*
        rootNotificationSettingsView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                v.removeOnLayoutChangeListener(this);
            }
        });*/


    }

    private void openSelectUniversityDialog(NotificationSettings settings) {

        Bundle b = new Bundle();
        b.putParcelable(SAConstants.NOTIFICATION_SETTINGS, settings);
        SelectUniversityDialog selectUniversityDialog = new SelectUniversityDialog();
        selectUniversityDialog.setArguments(b);
        selectUniversityDialog.show(getSupportFragmentManager(), "");

    }

    /**
     * called when a new university is selected from SelectUniversity Dialog box
     */
    public void createNewNotificationSettings(NotificationSettings settings) {

        settings.setApartmentName(null);
        settings.setApartmentType(null);
        settings.setGender(null);

        selectUniversityTv.setText(SAConstants.CHANGE_UNIVERSITY);

        Utilities.showView(findViewById(R.id.genderRadioGroup));
        Utilities.showView(findViewById(R.id.aptTypesChxBx));

        mApartmentNamesSet.clear();
        mApartmentTypesSet.clear();

        mOnCampusCheckboxes.removeAllViews();
        mOffCampusCheckboxes.removeAllViews();
        mDormsCheckboxes.removeAllViews();


        mOnCampusCheckbox.setChecked(false);
        mOffCampusCheckbox.setChecked(false);
        mDormsCheckbox.setChecked(false);

        Utilities.hideView(findViewById(R.id.onCampus));
        Utilities.hideView(findViewById(R.id.offCampus));
        Utilities.hideView(findViewById(R.id.dorms));

        mGenderRadioGroup.clearCheck();

        this.settings.setUniversityId(settings.getUniversityId());
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
            Utilities.hideView(findViewById(R.id.genderRadioGroup));
            Utilities.hideView(findViewById(R.id.aptTypesChxBx));

        } else {

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

            // after subscription is successful
            if (SAConstants.SUCCESS.equals(responseString)) {

                Bundle b = new Bundle();
                b.putString(SAConstants.ALERT_TEXT, "Subscription saved successfully");
                AlertDialogL alertDialogL = new AlertDialogL();
                alertDialogL.setArguments(b);
                alertDialogL.show(getSupportFragmentManager(), "");
                serverHasSettings = true;

                //server response with NotificationSettings
            } else if (!SAConstants.FAILURE.equals(response)) {
                Gson gson = new Gson();
                NotificationSettings settings = gson.fromJson(response, NotificationSettings.class);
                this.settings = settings;
                populateNotifications(settings);
                openNavTray();
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
    public void onBackPressed() {

        NotificationSettings settings = new NotificationSettings();
        settings.setUniversityId(this.settings.getUniversityId());

        settings.getApartmentName().addAll(mApartmentNamesSet);
        settings.getApartmentType().addAll(mApartmentTypesSet);

        String gender = null;
        int selectedId = mGenderRadioGroup.getCheckedRadioButtonId();
        if (selectedId != -1) {
            RadioButton genderRadioButton = (RadioButton) findViewById(selectedId);
            gender = String.valueOf(genderRadioButton.getText());
        }
        settings.setGender(gender);

        // setting these to null because the default object from JSON has null values
        if (settings.getApartmentType().isEmpty()) {
            settings.setApartmentType(null);
        }
        if (settings.getApartmentName().isEmpty()) {
            settings.setApartmentName(null);
        }

        if (settings.getGender() != null && settings.getGender().equals("")) {
            settings.setGender(null);
        }

        if (!this.settings.equals(settings)) {
            Bundle b = new Bundle();
            b.putString(SAConstants.ALERT_TEXT, SAConstants.SAVE_SETTINGS_PROMPT);
            SaveSettingsPrompt saveSettingsPrompt = new SaveSettingsPrompt();
            saveSettingsPrompt.setArguments(b);
            saveSettingsPrompt.show(getSupportFragmentManager(), "");
        } else {
            super.onBackPressed();
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
