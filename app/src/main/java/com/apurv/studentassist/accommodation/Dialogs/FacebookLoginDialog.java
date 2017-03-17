package com.apurv.studentassist.accommodation.Dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;

import com.apurv.studentassist.R;

/**
 * Created by akamalapuri on 3/16/2017.
 */

public class FacebookLoginDialog extends DialogFragment {
    private View pageView;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        pageView = inflater.inflate(R.layout.activity_main, null);
        builder.setView(pageView);

        return builder.create();

    }
}
