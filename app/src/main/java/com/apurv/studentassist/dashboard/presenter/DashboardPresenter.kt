package com.apurv.studentassist.dashboard.presenter

import com.apurv.studentassist.base.BasePresenter
import com.apurv.studentassist.dashboard.model.Recipes
import com.apurv.studentassist.dashboard.service.IDashboardService
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
                        .switchMap(Function<String, ObservableSource<Recipes>> { it ->
                            return@Function dashboardService.getMovies()
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ recipes ->
                            L.m("came here" + recipes.similar?.results?.get(0)?.name)
                        }, { error -> L.m("ello" + error) })
        )
    }

    override fun getFlashCards() {
    }


}