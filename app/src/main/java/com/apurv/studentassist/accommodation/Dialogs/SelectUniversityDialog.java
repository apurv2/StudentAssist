package com.apurv.studentassist.accommodation.Dialogs;

import android.app.Dialog;
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

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by akamalapuri on 7/29/2015.
 */
public class SelectUniversityDialog extends DialogFragment {


    @Bind(R.id.univ1)
    RadioButton radioButton1;

    @Bind(R.id.univ2)
    RadioButton radioButton2;

    @Bind(R.id.univ3)
    RadioButton radioButton3;

    @Bind(R.id.univ4)
    RadioButton radioButton4;

    @Bind(R.id.universityRadioGroup)
    RadioGroup universityRadioGroup;

    @Bind(R.id.sendUniversities)
    FloatingActionButton sendUniversities;


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
        String selectedUniversityName = bundle.getParcelable(SAConstants.SELECTED_UNIVERSITY_NAME);

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


            if (null != selectedUniversityName && !selectedUniversityName.equals("") && selectedUniversityName.equals(universityName)) {
                radioButtons.get(index).setSelected(true);
            }

            index++;
        }

        sendUniversities.setOnClickListener(v -> {

            NotificationSettingsActivity parentActivity = (NotificationSettingsActivity)
                    getActivity();
            String selectedUnivName = "";
            int selectedId = universityRadioGroup.getCheckedRadioButtonId();
            if (selectedId != -1) {
                RadioButton genderRadioButton = (RadioButton) pageView.findViewById(selectedId);
                selectedUnivName = String.valueOf(genderRadioButton.getText());
            }
            for (RApartmentNamesInUnivs universityName : universityNames) {
                if (selectedUnivName.equals(universityName.getUniversityName())) {

                    settings.setUniversityId(universityName.getUniversityId());
                    parentActivity.createNewNotificationSettings(settings);
                }
            }

            dismiss();

        });


        universityRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {

            Utilities.revealShow(pageView.findViewById(R.id.sendUniversities));
        });

        // Create the AlertDialog object and return it
        return builder.create();
    }
}
