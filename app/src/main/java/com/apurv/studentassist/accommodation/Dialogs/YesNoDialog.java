package com.apurv.studentassist.accommodation.Dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.apurv.studentassist.R;
import com.apurv.studentassist.accommodation.activities.NotificationSettingsActivity;

/**
 * Created by akamalapuri on 7/29/2015.
 */
public class YesNoDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the Builder class for convenient dialog construction
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View pageView = inflater.inflate(R.layout.fb_option, null);
        builder.setView(pageView);


        Button ok = (Button) pageView.findViewById(R.id.okButton);
        Button cancel = (Button) pageView.findViewById(R.id.cancel);


        ok.setOnClickListener(v -> {

            NotificationSettingsActivity parentActivity = (NotificationSettingsActivity) getActivity();
            parentActivity.changeUniversity();
            getDialog().dismiss();

        });

        cancel.setOnClickListener(v -> getDialog().dismiss());


        // Create the AlertDialog object and return it
        return builder.create();
    }


}

