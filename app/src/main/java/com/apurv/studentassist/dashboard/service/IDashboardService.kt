package com.apurv.studentassist.dashboard.service


import com.apurv.studentassist.dashboard.model.Recipes
import io.reactivex.Observable
import retrofit2.http.GET

interface IDashboardService {

    @GET("similar?q=red+hot+chili+peppers%2C+pulp+fiction")
    fun getMovies(): Observable<Recipes>
}