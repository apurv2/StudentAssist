package com.apurv.studentassist.accommodation.activities;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.apurv.studentassist.R;
import com.apurv.studentassist.accommodation.adapters.ImageSliderAdapter;
import com.apurv.studentassist.util.SAConstants;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PhotosViewActivity extends AppCompatActivity implements View.OnClickListener {


    @Bind(R.id.photosViewPager)
    ViewPager mPager;
    private ArrayList<Integer> imagesArray = new ArrayList<Integer>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos_view);
        ButterKnife.bind(this);

        List imageUrls = getIntent().getExtras().getParcelableArrayList(SAConstants.ACCOMMODATION_ADD_PHOTOS);

        imagesArray.add(R.drawable.ic_fbmessenger);
        imagesArray.add(R.drawable.ic_post);
        imagesArray.add(R.drawable.ic_us_dollar_256);

        init(imageUrls);
    }

    private void init(List imageUrls) {


        mPager.setAdapter(new ImageSliderAdapter(this, imageUrls));


    }

    @Override
    public void onClick(View v) {
        final int vis = mPager.getSystemUiVisibility();
        if ((vis & View.SYSTEM_UI_FLAG_LOW_PROFILE) != 0) {
            mPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        } else {
            mPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        }
    }
}
