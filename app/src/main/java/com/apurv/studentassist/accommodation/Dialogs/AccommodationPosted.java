package com.apurv.studentassist.accommodation.Dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.apurv.studentassist.R;
import com.apurv.studentassist.accommodation.activities.PostAccomodationActivity;
import com.apurv.studentassist.util.SAConstants;

/**
 * Created by akamalapuri on 7/29/2015.
 */
public class AccommodationPosted extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the Builder class for convenient dialog construction
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        setCancelable(false);
        View pageView = inflater.inflate(R.layout.dialog_alert, null);
        builder.setView(pageView);

        Bundle bundle = this.getArguments();
        final String text = bundle.getString(SAConstants.ALERT_TEXT);

        TextView textView = (TextView) pageView.findViewById(R.id.text);
        textView.setText(text);

        Button okButton = (Button) pageView.findViewById(R.id.ok_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostAccomodationActivity parentActivity = (PostAccomodationActivity) getActivity();
                parentActivity.closeActivity(true);
                dismiss();
            }
        });


        // Create the AlertDialog object and return it
        return builder.create();
    }
}
