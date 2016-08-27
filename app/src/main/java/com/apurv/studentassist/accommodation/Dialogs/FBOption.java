package com.apurv.studentassist.accommodation.Dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.apurv.studentassist.R;
import com.apurv.studentassist.accommodation.activities.PostAccomodationActivity;
import com.apurv.studentassist.util.Alerts;
import com.apurv.studentassist.util.SAConstants;

/**
 * Created by akamalapuri on 7/29/2015.
 */
public class FBOption extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the Builder class for convenient dialog construction
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View pageView = inflater.inflate(R.layout.fb_option, null);
        builder.setView(pageView);

        final EditText facebookIdEditText = (EditText) pageView.findViewById(R.id.facebookId);

        Button ok = (Button) pageView.findViewById(R.id.okButton);
        Button cancel = (Button) pageView.findViewById(R.id.no_im_good);


        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String facebookId = facebookIdEditText.getText().toString();

                if (facebookId != null || !facebookId.equals("")) {

                   /* PostAccomodationActivity parentActivity = (PostAccomodationActivity) getActivity();
                    parentActivity.postVacancy(facebookId);
                    getDialog().dismiss();
*/

                } else {
                    Bundle b = new Bundle();
                    b.putString(SAConstants.ALERT_TEXT, Alerts.errors.get(7));
                    AlertDialogL errorDialog = new AlertDialogL();
                    errorDialog.setArguments(b);
                    errorDialog.show(getActivity().getSupportFragmentManager(), "");
                }

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostAccomodationActivity parentActivity = (PostAccomodationActivity) getActivity();
                // parentActivity.postVacancy("");
                getDialog().dismiss();
            }
        });


        // Create the AlertDialog object and return it
        return builder.create();
    }


}

