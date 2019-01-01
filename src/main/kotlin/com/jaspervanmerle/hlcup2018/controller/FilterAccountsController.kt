package com.jaspervanmerle.hlcup2018.controller

import io.ktor.application.ApplicationCall
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.response.respondText

object FilterAccountsController : Controller() {
    init {
        validParameters += arrayOf(
            "sex_eq",
            "email_domain", "email_lt", "email_gt",
            "status_eq", "status_neq",
            "fname_eq", "fname_any", "fname_null",
            "sname_eq", "sname_starts", "sname_null",
            "phone_code", "phone_null",
            "country_eq", "country_null",
            "city_eq", "city_any", "city_null",
            "birth_lt", "birth_gt", "birth_year",
            "interests_contains", "interests_any",
            "likes_contains",
            "premium_now", "premium_null",
            "limit"
        )
    }

    override suspend fun processCall(call: ApplicationCall) {
        call.respondText("""{"accounts": []}""", ContentType.Application.Json, HttpStatusCode.fromValue(200))
    }
}
