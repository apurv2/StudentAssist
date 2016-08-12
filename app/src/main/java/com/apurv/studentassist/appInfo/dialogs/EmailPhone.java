package com.apurv.studentassist.appInfo.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.apurv.studentassist.R;
import com.apurv.studentassist.accommodation.Dialogs.AlertDialogL;
import com.apurv.studentassist.accommodation.activities.HomeScreenActivity;
import com.apurv.studentassist.util.Alerts;
import com.apurv.studentassist.util.SAConstants;

/**
 * Created by akamalapuri on 7/29/2015.
 */
public class EmailPhone extends DialogFragment {

    EditText emailId = null, phoneNumber = null;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the Builder class for convenient dialog construction
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View pageView = inflater.inflate(R.layout.email_phone, null);
        builder.setView(pageView);

        // tell the activity that the dialog is open
        final HomeScreenActivity parentActivity = (HomeScreenActivity) getActivity();
        parentActivity.dialog = true;

        final EditText emailIdL = (EditText) pageView.findViewById(R.id.emailId);
        final EditText phoneNumberL = (EditText) pageView.findViewById(R.id.phoneNo);
        emailId = emailIdL;
        phoneNumber = phoneNumberL;

        if (savedInstanceState != null) {
            emailIdL.setText(savedInstanceState.getString(SAConstants.EMAIL_ID));
            phoneNumberL.setText(savedInstanceState.getString(SAConstants.PHONE_NUMBER));
        }

        Button ok = (Button) pageView.findViewById(R.id.okButton);

        setCancelable(false);


        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailIdL.getText().toString();
                String phone = phoneNumberL.getText().toString();


                if (validatePhoneAndEmail(email, phone)) {


                    HomeScreenActivity parent = (HomeScreenActivity) getActivity();
                    parent.emailPhoneCallback(email, phone);

                    parentActivity.dialog = true;
                    getDialog().dismiss();


                }

            }
        });


        Bundle bundle = this.getArguments();
        Boolean fetch = bundle.getBoolean(SAConstants.FETCH);

        if (fetch) {
            emailId.setText(bundle.getString(SAConstants.EMAIL_ID));
            phoneNumber.setText(bundle.getString(SAConstants.PHONE_NUMBER));
        }


        // Create the AlertDialogL object and return it
        return builder.create();
    }


    private void basicAlert(String message) {

        Bundle b = new Bundle();
        b.putString(SAConstants.ALERT_TEXT, message);
        AlertDialogL errorDialog = new AlertDialogL();
        errorDialog.setArguments(b);
        errorDialog.show(getActivity().getSupportFragmentManager(), "");

    }

    private boolean validatePhoneAndEmail(String email, String phoneNo) {


        if (phoneNo.isEmpty() || email.isEmpty()) {


            String message = Alerts.errors.get(9);
            basicAlert(message);

            return false;

        } else if (phoneNo.length() < 10) {

            String message = Alerts.errors.get(10);
            basicAlert(message);

            return false;


        } else if (phoneNo.length() < 11) {


            String message = Alerts.errors.get(11);
            basicAlert(message);

            return false;


        }
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);


        if (emailId != null || phoneNumber != null) {

            outState.putString(SAConstants.EMAIL_ID, emailId.getText().toString());
            outState.putString(SAConstants.PHONE_NUMBER, phoneNumber.getText().toString());


        }

    }
}

