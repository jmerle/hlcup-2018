package com.jaspervanmerle.hlcup2018.model

import com.google.gson.annotations.SerializedName

data class Premium(
    val start: Int,
    @SerializedName("finish") val end: Int
)
