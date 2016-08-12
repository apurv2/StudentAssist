package com.apurv.studentassist.accommodation.Dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.apurv.studentassist.R;
import com.apurv.studentassist.accommodation.Interfaces.DialogCallback;
import com.apurv.studentassist.util.ErrorReporting;
import com.apurv.studentassist.util.SAConstants;

import java.io.UnsupportedEncodingException;


public class DeleteOption extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View pageView = inflater.inflate(R.layout.fragment_delete_option, null);
        builder.setView(pageView);

        final DialogCallback dialogCallback = (DialogCallback) getTargetFragment();

        Button delete = (Button) pageView.findViewById(R.id.ok_button);
        Button exit = (Button) pageView.findViewById(R.id.dont_delete);


        Bundle bundle = this.getArguments();
        final int position = bundle.getInt(SAConstants.POSITION);


        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    dialogCallback.OnDialogResponse(position);
                    dismiss();
                } catch (UnsupportedEncodingException e) {
                    ErrorReporting.logReport(e);
                }
            }
        });


        exit.setOnClickListener(new View.OnClickListener() {
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
