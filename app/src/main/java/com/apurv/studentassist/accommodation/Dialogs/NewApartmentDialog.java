package com.apurv.studentassist.accommodation.Dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.apurv.studentassist.R;
import com.apurv.studentassist.util.SAConstants;
import com.apurv.studentassist.accommodation.activities.PostAccomodationActivity;

public class NewApartmentDialog extends DialogFragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getDialog() != null) {
            getDialog().setCanceledOnTouchOutside(true);
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View pageView = inflater.inflate(R.layout.fragment_new_apartment_dialog, null);
        builder.setView(pageView);

        if (getDialog() != null) {
            getDialog().setCanceledOnTouchOutside(true);
        }


        Button submitButton = (Button) pageView.findViewById(R.id.okButton);
        Button cancelButton = (Button) pageView.findViewById(R.id.cancelButton);
        final EditText editText = (EditText) pageView.findViewById(R.id.apartmentNameEditText);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String apartmentName = editText.getText().toString();

                if (apartmentName.length() < 0) {
                    Toast.makeText(getActivity().getApplicationContext(), SAConstants.APARTMENT_NAME_PROMPT, Toast.LENGTH_LONG).show();

                } else {

                    PostAccomodationActivity parentActivity = (PostAccomodationActivity) getActivity();
                    parentActivity.aptNameCallback(apartmentName);

                    getDialog().dismiss();

                }


            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PostAccomodationActivity parentActivity = (PostAccomodationActivity) getActivity();
                parentActivity.apartmentNameCancelCallback();

                getDialog().dismiss();

            }
        });


        // Create the AlertDialog object and return it
        return builder.create();
    }


}
