package com.apurv.studentassist.dashboard.view

import com.apurv.studentassist.accommodation.classes.FlashCardsResponseDTO
import com.apurv.studentassist.base.BaseView


interface IDashboardView : BaseView {
    fun show();
    fun hide();
    fun searchTextChangeListener();
    fun populateApartmentsRecyclerView(apartments : FlashCardsResponseDTO);
}