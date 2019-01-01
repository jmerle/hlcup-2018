package com.jaspervanmerle.hlcup2018.controller

import io.ktor.application.ApplicationCall
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.response.respondText

object SuggestAccountsController : Controller() {
    init {
        validParameters += arrayOf(
            "country", "city", "limit"
        )
    }

    override suspend fun processCall(call: ApplicationCall) {
        val id = call.parameters["id"]!!
        call.respondText("""{"accounts": []}""", ContentType.Application.Json, HttpStatusCode.fromValue(200))
    }
}
