package com.apurv.studentassist.accommodation.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.apurv.studentassist.R;
import com.apurv.studentassist.accommodation.adapters.ImageSliderAdapter;
import com.apurv.studentassist.util.SAConstants;
import com.apurv.studentassist.util.Utilities;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PhotosViewActivity extends AppCompatActivity implements View.OnClickListener {


    @Bind(R.id.photosViewPager)
    ViewPager mPager;
    List imageUrls;
    ImageSliderAdapter mImageSliderAdapter;
    private ArrayList<Integer> deletedPhotos;
    int position;
    String callingActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos_view);
        ButterKnife.bind(this);
        deletedPhotos = new ArrayList<>();

        Toolbar toolbar;
        toolbar = (Toolbar) findViewById(R.id.applicationBar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Navigation Icon
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhotosViewActivity.super.onBackPressed();
            }
        });

        callingActivity = getIntent().getExtras().getString(SAConstants.CALLING_ACTIVITY);
        imageUrls = getIntent().getExtras().getParcelableArrayList(SAConstants.ACCOMMODATION_ADD_PHOTOS);
        if (savedInstanceState != null) {
            deletedPhotos = savedInstanceState.getIntegerArrayList(SAConstants.DELETED_PHOTOS);
            position = savedInstanceState.getInt(SAConstants.POSITION);

            for (int deletedPhoto : deletedPhotos) {
                imageUrls.remove(deletedPhoto);
            }

        } else {

            position = Integer.parseInt(String.valueOf(getIntent().getExtras().get(SAConstants.POSITION)));
        }

        String imageType = String.valueOf(getIntent().getExtras().get(SAConstants.IMAGE_TYPE));


        init(imageUrls, imageType, position);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.buttonColourBlack));
        }

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);


        int mToolbarHeight = getToolbarHeight() + getStatusBarHeight();

        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) toolbar.getLayoutParams();
        layoutParams.height = mToolbarHeight;
        toolbar.setLayoutParams(layoutParams);
        toolbar.requestLayout();


        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        upArrow.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
    }

    public int getToolbarHeight() {
        TypedValue tv = new TypedValue();
        int actionBarHeight = 0;
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
        return actionBarHeight;
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private void init(List imageUrls, String imageType, int position) {

        mImageSliderAdapter = new ImageSliderAdapter(this, imageUrls, imageType);
        mPager.setAdapter(mImageSliderAdapter);
        mPager.setCurrentItem(position);

        GestureDetector tapGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {


            @Override
            public boolean onSingleTapUp(MotionEvent e) {


                toggleHideyBar();
                return super.onSingleTapUp(e);


            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                return true;

            }
        });

        mPager.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                tapGestureDetector.onTouchEvent(event);
                return false;
            }
        });


    }

    @Override
    public void onClick(View v) {

        toggleHideyBar();


    }

    public void toggleHideyBar() {

        // BEGIN_INCLUDE (get_current_ui_flags)
        // The UI options currently enabled are represented by a bitfield.
        // getSystemUiVisibility() gives us that bitfield.
        int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
        int newUiOptions = uiOptions;
        // END_INCLUDE (get_current_ui_flags)
        // BEGIN_INCLUDE (toggle_ui_flags)
        boolean isImmersiveModeEnabled =
                ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE) == uiOptions);


        if (!isImmersiveModeEnabled) {

            Utilities.hideView(findViewById(R.id.applicationBar));
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                            | View.SYSTEM_UI_FLAG_IMMERSIVE);

        } else {
            Utilities.showView(findViewById(R.id.applicationBar));

            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }


    }

    @OnClick(R.id.photosViewPager)
    public void imageClick(View view) {

        toggleHideyBar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.photos_view, menu);

        if (!SAConstants.POST_ACCOMMODATION_ACTIVITY.equals(callingActivity)) {

            menu.findItem(R.id.delete_photo).setVisible(false);

        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_photo:
                deletePhoto();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deletePhoto() {

        deletedPhotos.add(mPager.getCurrentItem());
        mImageSliderAdapter.removeItem(mPager.getCurrentItem());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntegerArrayList(SAConstants.DELETED_PHOTOS, deletedPhotos);
        outState.putInt(SAConstants.POSITION, position);

    }


    @Override
    public void onBackPressed() {
        Intent resultIntent = new Intent();
        resultIntent.putIntegerArrayListExtra(SAConstants.DELETED_PHOTOS, deletedPhotos);

        resultIntent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);

        if (getParent() == null) {
            setResult(Activity.RESULT_OK, resultIntent);
        } else {
            getParent().setResult(Activity.RESULT_OK, resultIntent);
        }
        PhotosViewActivity.super.onBackPressed();
    }
}
