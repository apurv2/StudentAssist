package com.apurv.studentassist.accommodation.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.apurv.studentassist.R;
import com.apurv.studentassist.util.SAConstants;

/**
 * Created by akamalapuri on 7/29/2015.
 */
public class ImageViewDialog extends DialogFragment {

    Bitmap bitmap;

    @Override
    public void onStart() {
        super.onStart();

        // safety check
        if (getDialog() == null) {
            return;
        }

        int dialogWidth = 1000; // specify a value here
        int dialogHeight = 1000; // specify a value here

        getDialog().getWindow().setLayout(dialogWidth, dialogHeight);


    }

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
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.full_size_image_layout, null);
        builder.setView(view);


        Bundle bundle = this.getArguments();
        Bitmap profilePicture = bundle.getParcelable(SAConstants.PROFILE_PIC);


        ImageView imageView = (ImageView) view.findViewById(R.id.iv_preview_image);
        //  Bitmap receivedBitmap = BitmapFactory.decodeByteArray(clickedAdd.getBitmapByteArray(), 0, clickedAdd.getBitmapByteArray().length);
        imageView.setImageBitmap(profilePicture);


        // Create the AlertDialog object and return it
        return builder.create();
    }


}
