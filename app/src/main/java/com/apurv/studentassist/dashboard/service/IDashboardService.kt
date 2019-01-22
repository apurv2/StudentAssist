package com.apurv.studentassist.dashboard.service


import com.apurv.studentassist.accommodation.classes.FlashCardsResponseDTO
import io.reactivex.Observable
import retrofit2.http.POST

interface IDashboardService {

    @POST("getFlashCards")
    fun getFlashCards(flashCardsRequest: FlashCardsResponseDTO): Observable<FlashCardsResponseDTO>
}