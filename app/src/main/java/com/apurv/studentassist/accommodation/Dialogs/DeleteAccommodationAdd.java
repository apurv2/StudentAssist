package com.apurv.studentassist.accommodation.Dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.apurv.studentassist.R;
import com.apurv.studentassist.accommodation.activities.AdDetailsActivity;
import com.apurv.studentassist.util.ErrorReporting;


public class DeleteAccommodationAdd extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View pageView = inflater.inflate(R.layout.delete_accommodation_add_dialog, null);
        builder.setView(pageView);

        final AdDetailsActivity parentActivity = (AdDetailsActivity) getActivity();

        Button yes = (Button) pageView.findViewById(R.id.yes);
        Button no = (Button) pageView.findViewById(R.id.no);

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    if (parentActivity != null) {

                        parentActivity.deletePostCallback();
                    }
                    dismiss();


                } catch (Exception e) {
                    ErrorReporting.logReport(e);
                }
            }
        });


        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dismiss();
            }
        });


        // Create the AlertDialog object and return it
        return builder.create();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getDialog() != null) {
            getDialog().setCanceledOnTouchOutside(true);
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }


}
