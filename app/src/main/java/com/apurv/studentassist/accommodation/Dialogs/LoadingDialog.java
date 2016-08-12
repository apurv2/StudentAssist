package com.apurv.studentassist.accommodation.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.apurv.studentassist.R;
import com.apurv.studentassist.util.interfaces.LodingDialogInterface;
import com.apurv.studentassist.util.SAConstants;

/**
 * Created by akamalapuri on 11/5/2015.
 */
public class LoadingDialog extends DialogFragment {

    public LodingDialogInterface lodingDialogInterface;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the Builder class for convenient dialog construction
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        setRetainInstance(true);
        setCancelable(false);

        final View pageView = inflater.inflate(R.layout.loading_dialog, null);
        builder.setView(pageView);


        Bundle bundle = this.getArguments();
        final String text = bundle.getString(SAConstants.LOADER_TEXT);


        TextView loadingText = (TextView) pageView.findViewById(R.id.loading);
        loadingText.setText(text);


        // Create the AlertDialog object and return it
        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        lodingDialogInterface = (LodingDialogInterface) activity;
    }


}
