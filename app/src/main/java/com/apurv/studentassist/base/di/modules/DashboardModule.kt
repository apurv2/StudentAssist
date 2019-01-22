package com.apurv.studentassist.base.di.modules

import com.apurv.studentassist.accommodation.adapters.UniversitiesListAdapter
import com.apurv.studentassist.base.BasePresenter
import com.apurv.studentassist.dashboard.presenter.DashboardPresenter
import com.apurv.studentassist.dashboard.presenter.IDashboardPresenter
import com.apurv.studentassist.dashboard.service.IDashboardService
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable
import retrofit2.Retrofit

@Module
class DashboardModule {

    @Provides
    fun provideApiService(retrofit: Retrofit): IDashboardService {
        return retrofit.create(IDashboardService::class.java)
    }

    @Provides
    fun provideBasePresenter(compositeDisposable: CompositeDisposable): BasePresenter {
        return BasePresenter(compositeDisposable)
    }

    @Provides
    fun provideDasboardresenter(dasboardPresenter: DashboardPresenter): IDashboardPresenter {
        return dasboardPresenter
    }

    @Provides
    fun provideUniversitiesListAdapter(): UniversitiesListAdapter {
        return UniversitiesListAdapter(ArrayList())
    }
}