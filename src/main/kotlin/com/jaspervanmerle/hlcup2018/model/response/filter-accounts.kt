package com.jaspervanmerle.hlcup2018.model.response

import com.google.gson.annotations.SerializedName

data class FilterAccountsAccount(
    val id: Int,
    val email: String,
    @SerializedName("fname") var firstName: String? = null,
    @SerializedName("sname") var lastName: String? = null,
    var phone: String? = null,
    @SerializedName("sex") var gender: String? = null,
    var birth: Int? = null,
    var country: String? = null,
    var city: String? = null,
    var status: String? = null
)

data class FilterAccountsResponse(val accounts: List<FilterAccountsAccount>)
