package com.apurv.studentassist.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.apurv.studentassist.R;
import com.apurv.studentassist.accommodation.Dialogs.AlertDialogL;
import com.apurv.studentassist.accommodation.Dialogs.LoadingDialog;
import com.apurv.studentassist.accommodation.classes.StudentAssistApplication;
import com.apurv.studentassist.internet.Network;

import java.io.File;


/**
 * Created by apurv on 6/8/15.
 */
public class Utilities {


    public static Animation fadeIn;
    public static Animation fadeOut;
    public static Animation slideUp;
    public static Animation slideDown;
    public static Animation rotateClockwise;
    public static Animation rotateAnticlockwise;
    public static Animation showFabLayout;
    public static Animation hideFabLayout;


    static {
        fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
        fadeIn.setDuration(200);

        fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator()); //and this
        fadeOut.setDuration(100);

        slideUp = AnimationUtils.loadAnimation(StudentAssistApplication.getmInstance(), R.anim.bottom_up);
        slideUp.setDuration(100);

        slideDown = AnimationUtils.loadAnimation(StudentAssistApplication.getmInstance(), R.anim.slide_out_bottom);
        slideDown.setDuration(100);

        rotateClockwise = AnimationUtils.loadAnimation(StudentAssistApplication.getmInstance(), R.anim.rotate_clockwise);
        rotateClockwise.setDuration(100);

        rotateAnticlockwise = AnimationUtils.loadAnimation(StudentAssistApplication.getmInstance(), R.anim.rotate_anticlockwise);
        showFabLayout = AnimationUtils.loadAnimation(StudentAssistApplication.getmInstance(), R.anim.show_layout);
        hideFabLayout = AnimationUtils.loadAnimation(StudentAssistApplication.getmInstance(), R.anim.hide_layout);
    }


    public static void showView(View viewGroup, int viewId) {

        viewGroup.findViewById(viewId).setVisibility(View.VISIBLE);

    }

    public static void hideView(View viewGroup, int viewId) {

        viewGroup.findViewById(viewId).setVisibility(View.GONE);

    }

    public static void toggleView(View view) {

        if (view.getVisibility() == View.VISIBLE) {

            hideView(view);
        } else {
            showView(view);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void revealShow(View view) {

        if (view.getVisibility() != View.VISIBLE) {


            // get the center for the clipping circle
            int cx = view.getWidth() / 2;
            int cy = view.getHeight() / 2;

            // get the final radius for the clipping circle
            float finalRadius = (float) Math.hypot(cx, cy);

            // create the animator for this view (the start radius is zero)
            Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, finalRadius);

            // make the view visible and start the animation
            view.setVisibility(View.VISIBLE);
            anim.start();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void revealHide(View view) {

        if (view.getVisibility() == View.VISIBLE) {

            // previously visible view

            // get the center for the clipping circle
            int cx = view.getWidth() / 2;
            int cy = view.getHeight() / 2;

            // get the initial radius for the clipping circle
            float initialRadius = (float) Math.hypot(cx, cy);

            // create the animation (the final radius is zero)
            Animator anim =
                    ViewAnimationUtils.createCircularReveal(view, cx, cy, initialRadius, 0);

            // make the view invisible when the animation is done
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    view.setVisibility(View.INVISIBLE);
                }
            });

// start the animation
            anim.start();
        }
    }


    public static void toggleViewWithRevealAnimation(View view) {

        if (view.getVisibility() == View.VISIBLE) {
            revealHide(view);
        } else {
            revealShow(view);
        }
    }


    public static void toggleViewWithAnimation(View view, Animation anim1, Animation anim2) {

        if (view.getVisibility() == View.VISIBLE) {
            view.startAnimation(anim1);
            hideView(view);
        } else {
            view.startAnimation(anim2);
            showView(view);
        }
    }

    public static void hideViewUsingAnimation(View view, Animation fadeOut) {

        if (view.getVisibility() == View.VISIBLE) {

            view.startAnimation(fadeOut);
            hideView(view);
        }
    }

    public static void showViewUsingAnimation(View view, Animation showAnimation) {

        if (view.getVisibility() != View.VISIBLE) {
            view.startAnimation(showAnimation);
            showView(view);
        }
    }

    public static void rotateAnimation(View view, Animation rotateAnimation) {

        view.startAnimation(rotateAnimation);
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

    /**
     * @param url
     * @param imageView
     * @param textView  - optional
     */
    public static void loadImages(String url, ImageView imageView, TextView textView) {

        Network network;

        network = Network.getNetworkInstnace();
        ImageLoader mImageLoader = network.getmImageLoader();

        mImageLoader.get(url, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {


                Bitmap photo = response.getBitmap();
                if (photo != null && imageView != null) {

                    imageView.setImageBitmap(photo);
                    Utilities.showView(textView);
                }

            }

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

    }


}

