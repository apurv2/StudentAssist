package com.apurv.studentassist.dashboard.presenter

import com.apurv.studentassist.accommodation.classes.FlashCardsResponseDTO
import com.apurv.studentassist.base.BasePresenter
import com.apurv.studentassist.dashboard.service.IDashboardService
import com.apurv.studentassist.dashboard.view.IDashboardView
import com.apurv.studentassist.util.L
import io.reactivex.ObservableSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Function
import io.reactivex.functions.Predicate
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class DashboardPresenter @Inject constructor(private val compositeDisposable: CompositeDisposable)
    : BasePresenter(compositeDisposable), IDashboardPresenter {

    lateinit var dashboardView: IDashboardView

    override fun initializeView(dashBoardView: IDashboardView) {
        dashboardView = dashBoardView;
    }


    @Inject
    lateinit var dashboardService: IDashboardService;


    override fun search(subject: PublishSubject<String>) {
        compositeDisposable.add(
                subject
                        .debounce(300, TimeUnit.MILLISECONDS)
                        .filter(Predicate { it: String ->
                            return@Predicate it.isNotEmpty()
                        })
                        .distinctUntilChanged()
                        .switchMap(Function<String, ObservableSource<FlashCardsResponseDTO>> { it ->
                            return@Function dashboardService.getFlashCards(FlashCardsResponseDTO())
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ apartments: FlashCardsResponseDTO ->
                            dashboardView.populateApartmentsRecyclerView(apartments)
                        }, { error -> L.m("ello" + error) })
        )
    }

    override fun getFlashCards() {
    }


}