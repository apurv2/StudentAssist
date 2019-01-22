package com.apurv.studentassist.dashboard.presenter

import com.apurv.studentassist.dashboard.view.IDashboardView
import io.reactivex.subjects.PublishSubject

public interface IDashboardPresenter {

    fun getFlashCards();
    fun search(publishSubject: PublishSubject<String>);
    fun initializeView(dashBoardView : IDashboardView);
}