package com.apurv.studentassist.dashboard.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Recipes(
        @SerializedName("Similar")
        @Expose
        var similar: Similar)

