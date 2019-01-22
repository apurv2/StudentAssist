package com.apurv.studentassist.dashboard.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import com.apurv.studentassist.R
import com.apurv.studentassist.accommodation.adapters.UniversitiesListAdapter
import com.apurv.studentassist.accommodation.classes.FlashCardsResponseDTO
import com.apurv.studentassist.base.BaseActivity
import com.apurv.studentassist.dashboard.presenter.IDashboardPresenter
import dagger.android.AndroidInjection
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.dashboard.*
import javax.inject.Inject

class DashboardActivity : BaseActivity(), IDashboardView {

    @Inject
    lateinit var dashboardPresenter: IDashboardPresenter

    @Inject
    lateinit var dashboardAdapter: UniversitiesListAdapter;

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dashboard)
        dashboardPresenter.initializeView(this);


        dashboardPresenter.getFlashCards()
        searchTextChangeListener();
        todo();

    }

    private fun todo() {
        flashCardsRecyclerView.adapter = dashboardAdapter;

    }

    override fun show() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun hide() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun searchTextChangeListener() {

        val subject = PublishSubject.create<String>()
        search_bar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                subject.onNext(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        dashboardPresenter.search(subject)
    }

    override fun populateApartmentsRecyclerView(apartments: FlashCardsResponseDTO) {

        dashboardAdapter.addAll(apartments.accommodationCards!!);


    }


}
