package com.apurv.studentassist.dashboard.presenter

import io.reactivex.subjects.PublishSubject

public interface IDashboardPresenter {

    fun getFlashCards();
    fun search(publishSubject: PublishSubject<String>);
}