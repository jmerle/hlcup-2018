package com.jaspervanmerle.hlcup2018.model

import com.google.gson.annotations.SerializedName

data class Account(
    val id: Int,
    val email: String,
    @SerializedName("fname") val firstName: String?,
    @SerializedName("sname") val lastName: String?,
    val phone: String?,
    @SerializedName("sex") val gender: String,
    val birth: Int,
    val country: String?,
    val city: String?,
    val joined: Int,
    val status: String,
    val interests: List<String>?,
    val premium: Premium?,
    val likes: List<Like>?
)
