package com.apurv.studentassist.accommodation.Dialogs;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.apurv.studentassist.R;
import com.apurv.studentassist.accommodation.activities.NotificationSettingsActivity;
import com.apurv.studentassist.accommodation.classes.NotificationSettings;
import com.apurv.studentassist.accommodation.classes.RApartmentNamesInUnivs;
import com.apurv.studentassist.util.SAConstants;
import com.apurv.studentassist.util.Utilities;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by akamalapuri on 7/29/2015.
 */
public class SelectUniversityDialog extends DialogFragment {


    @BindView(R.id.univ1)
    RadioButton radioButton1;

    @BindView(R.id.univ2)
    RadioButton radioButton2;

    @BindView(R.id.univ3)
    RadioButton radioButton3;

    @BindView(R.id.univ4)
    RadioButton radioButton4;

    @BindView(R.id.universityRadioGroup)
    RadioGroup universityRadioGroup;

    @BindView(R.id.sendUniversities)
    FloatingActionButton sendUniversities;
    boolean univSelected = false;

    List<RadioButton> radioButtons = new ArrayList<>();


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the Builder class for convenient dialog construction
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View pageView = inflater.inflate(R.layout.university_select, null);
        builder.setView(pageView);
        ButterKnife.bind(this, pageView);


        Bundle bundle = this.getArguments();
        NotificationSettings settings = bundle.getParcelable(SAConstants.NOTIFICATION_SETTINGS);

        radioButtons.add(radioButton1);
        radioButtons.add(radioButton2);
        radioButtons.add(radioButton3);
        radioButtons.add(radioButton4);

        //The JSON data contains apartment names with universities
        List<RApartmentNamesInUnivs> universityNames = settings.getApartmentNames();

        int index = 0;
        for (RApartmentNamesInUnivs universityName : universityNames) {

            Utilities.showView(radioButtons.get(index));
            radioButtons.get(index).setText(universityName.getUniversityName());

            //logic to prepopulate univesity Name radio button
            if (settings.getUniversityId() != -1 && universityName.getUniversityId() == settings.getUniversityId()) {
                universityRadioGroup.check(radioButtons.get(index).getId());

                pageView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                        v.removeOnLayoutChangeListener(this);
                        Utilities.revealShow(pageView.findViewById(R.id.sendUniversities));
                    }
                });
            }
            index++;
        }

        sendUniversities.setOnClickListener(v -> {

            NotificationSettingsActivity parentActivity = (NotificationSettingsActivity)
                    getActivity();
            int selectedId = universityRadioGroup.getCheckedRadioButtonId();

            if (selectedId != -1) {
                univSelected = true;
                RadioButton universityRadioButton = (RadioButton) pageView.findViewById(selectedId);
                int idx = universityRadioGroup.indexOfChild(universityRadioButton);

                settings.setUniversityId(universityNames.get(idx).getUniversityId());
                parentActivity.createNewNotificationSettings(settings);
                dismiss();
            }
        });

        universityRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            Utilities.revealShow(pageView.findViewById(R.id.sendUniversities));
        });

        // Create the AlertDialog object and return it
        return builder.create();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (!univSelected) {
            NotificationSettingsActivity parentActivity = (NotificationSettingsActivity)
                    getActivity();
            parentActivity.openNavTray();
        }
    }
}
