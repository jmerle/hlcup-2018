package com.jaspervanmerle.hlcup2018.controller

import io.ktor.application.ApplicationCall
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.response.respondText

object GroupAccountsController : Controller() {
    init {
        validParameters += arrayOf(
            "id",
            "fname",
            "sname",
            "phone",
            "sex",
            "birth",
            "country",
            "city",
            "joined",
            "status",
            "interests",
            "likes",
            "keys",
            "limit",
            "order"
        )
    }

    override suspend fun processCall(call: ApplicationCall) {
        call.respondText("""{"groups": []}""", ContentType.Application.Json, HttpStatusCode.fromValue(200))
    }
}
