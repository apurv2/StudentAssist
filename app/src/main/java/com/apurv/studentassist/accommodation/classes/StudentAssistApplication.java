package com.apurv.studentassist.accommodation.classes;

import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.LeakCanary;

/**
 * Created by apurv on 6/3/15.
 */
public class StudentAssistApplication extends Application {

    private static StudentAssistApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        LeakCanary.install(this);
        mInstance = this;
    }

    public static StudentAssistApplication getmInstance() {
        return mInstance;

    }

    public static Context getAppContext() {
        return mInstance.getApplicationContext();
    }
}
