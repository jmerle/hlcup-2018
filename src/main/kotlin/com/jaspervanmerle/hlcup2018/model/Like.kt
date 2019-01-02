package com.jaspervanmerle.hlcup2018.model

import com.google.gson.annotations.SerializedName

data class Like(
    @SerializedName("id") val toId: Int,
    @SerializedName("ts") val timestamp: Int
)
