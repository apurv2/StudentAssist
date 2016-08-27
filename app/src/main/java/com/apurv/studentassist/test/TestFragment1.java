package com.apurv.studentassist.test;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.apurv.studentassist.R;


public class TestFragment1 extends Fragment {

    View mView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        mView = inflater.inflate(R.layout.fragment_test_fragment1, container, false);

        return mView;


    }

}
