package com.apurv.studentassist.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.apurv.studentassist.accommodation.Dialogs.AlertDialogL;
import com.apurv.studentassist.accommodation.Dialogs.LoadingDialog;

import java.io.File;

/**
 * Created by apurv on 6/8/15.
 */
public class Utilities {


    public static void showView(View viewGroup, int viewId) {

        viewGroup.findViewById(viewId).setVisibility(View.VISIBLE);


    }

    public static void hideView(View viewGroup, int viewId) {

        viewGroup.findViewById(viewId).setVisibility(View.GONE);


    }

    public static void hideView(View view) {

        view.setVisibility(View.GONE);
    }

    public static void showView(View view) {

        view.setVisibility(View.VISIBLE);

    }

    public static void changeToInvisibleView(View view) {

        view.setVisibility(View.INVISIBLE);
    }

    public static LoadingDialog showLoadingDialog(String loaderText, FragmentManager manager) {


        LoadingDialog loadingDialog = new LoadingDialog();

        Bundle bundle = new Bundle();
        bundle.putString(SAConstants.LOADER_TEXT, loaderText);
        loadingDialog.setArguments(bundle);
        loadingDialog.show(manager, "");

        return loadingDialog;
    }

    /**
     * Utility method to display an alert dialog with a custom message
     *
     * @param mLoaderText      - custom message to be shown on the alert dialog
     * @param mFragmentManager - instance of the FragmentManager sent from the calling activity.
     * @return AlertDialog instance after launching it
     */
    public static AlertDialogL showALertDialog(String mLoaderText, FragmentManager mFragmentManager) {

        //setting a bundle with message parameter to send to Dialog fragment
        Bundle mMessageBundle = new Bundle();
        mMessageBundle.putString(SAConstants.ALERT_TEXT, mLoaderText);

        // creating instance of dialog fragment and setting the message bundle.
        AlertDialogL mErrorDialog = new AlertDialogL();
        mErrorDialog.setArguments(mMessageBundle);

        // launching the dialog fragment
        mErrorDialog.show(mFragmentManager, "");


        return mErrorDialog;
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(File file,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(file.getAbsolutePath(), options);
    }


}
