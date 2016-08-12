package com.apurv.studentassist.accommodation.Dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.apurv.studentassist.R;
import com.apurv.studentassist.accommodation.Interfaces.DialogCallback;
import com.apurv.studentassist.util.ErrorReporting;
import com.apurv.studentassist.util.SAConstants;

/**
 * Created by akamalapuri on 7/29/2015.
 */
public class SimpleSubscriptionAlertDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the Builder class for convenient dialog construction
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View pageView = inflater.inflate(R.layout.simple_subscription_alert_dialog, null);
        builder.setView(pageView);


        final DialogCallback dialogCallback = (DialogCallback) getTargetFragment();


        Bundle bundle = this.getArguments();
        final String text = bundle.getString(SAConstants.ALERT_TEXT);

        TextView textView = (TextView) pageView.findViewById(R.id.text);
        textView.setText(text);


        Button yes = (Button) pageView.findViewById(R.id.yes);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    dialogCallback.OnDialogResponse(0);
                    dismiss();
                } catch (Exception e) {
                    ErrorReporting.logReport(e);
                }
            }
        });


        Button no = (Button) pageView.findViewById(R.id.no);
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
