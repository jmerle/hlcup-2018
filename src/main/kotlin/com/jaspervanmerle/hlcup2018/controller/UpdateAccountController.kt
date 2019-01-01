package com.jaspervanmerle.hlcup2018.controller

import io.ktor.application.ApplicationCall
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.response.respondText

object UpdateAccountController : Controller() {
    override suspend fun processCall(call: ApplicationCall) {
        val id = call.parameters["id"]!!
        call.respondText("{}", ContentType.Application.Json, HttpStatusCode.fromValue(202))
    }
}
