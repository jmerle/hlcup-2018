package com.jaspervanmerle.hlcup2018.controller

import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond

abstract class Controller {
    protected val validParameters: HashSet<String> = hashSetOf("query_id")

    protected abstract suspend fun processCall(call: ApplicationCall)

    suspend fun process(call: ApplicationCall) {
        for (name in call.request.queryParameters.names()) {
            if (name !in validParameters) {
                call.respond(HttpStatusCode.fromValue(400), "")
                return
            }
        }

        processCall(call)
    }
}
