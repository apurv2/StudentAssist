package com.apurv.studentassist.dashboard.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Similar(
        @SerializedName("Results")
        @Expose
        var results: MutableList<Results>? = null)