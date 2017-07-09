package com.apurv.studentassist.accommodation.Dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.apurv.studentassist.R;
import com.apurv.studentassist.accommodation.activities.NotificationSettingsActivity;
import com.apurv.studentassist.util.SAConstants;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by akamalapuri on 7/29/2015.
 */
public class UnsubsribePromptDialog extends DialogFragment {


    @Bind(R.id.text)
    TextView prompt;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the Builder class for convenient dialog construction
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View pageView = inflater.inflate(R.layout.fb_option, null);
        builder.setView(pageView);
        ButterKnife.bind(this, pageView);


        Bundle bundle = this.getArguments();
        final String text = bundle.getString(SAConstants.ALERT_TEXT);
        prompt.setText(text);



        Button ok = (Button) pageView.findViewById(R.id.okButton);
        Button cancel = (Button) pageView.findViewById(R.id.cancel);


        ok.setOnClickListener(v -> {

            NotificationSettingsActivity parentActivity = (NotificationSettingsActivity) getActivity();
            parentActivity.unSubscribeNotifications();
            getDialog().dismiss();

        });

        cancel.setOnClickListener(v -> getDialog().dismiss());


        // Create the AlertDialog object and return it
        return builder.create();
    }


}

