package com.apurv.studentassist.accommodation.Dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.apurv.studentassist.R;
import com.apurv.studentassist.util.Utilities;

/**
 * Created by akamalapuri on 11/5/2015.
 */
public class TakeDownPost extends DialogFragment {

    int state = 0;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the Builder class for convenient dialog construction
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        final View pageView = inflater.inflate(R.layout.take_this_post_down, null);
        builder.setView(pageView);

        Utilities.hideView(pageView.findViewById(R.id.textView));


        Button okButton = (Button) pageView.findViewById(R.id.okButton);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                state++;
                if (state == 1) {
                    Utilities.hideView(pageView.findViewById(R.id.editText));
                    Utilities.showView(pageView.findViewById(R.id.textView));
                }
                if (state == 2) {
                    // send notification to System Admin.


                    dismiss();
                }

            }
        });


        // Create the AlertDialog object and return it
        return builder.create();
    }


}
