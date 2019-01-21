package com.apurv.studentassist.base.di.modules

import com.apurv.studentassist.dashboard.view.DashboardActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector


/**
 * Created by vicky on 1/1/18.
 */
@Module
abstract class ActivityBindingModule {

    @ContributesAndroidInjector(modules = [(DashboardModule::class)])
    abstract fun bindHomeActivity(): DashboardActivity
}