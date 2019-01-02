package com.jaspervanmerle.hlcup2018.controller

import com.jaspervanmerle.hlcup2018.gson
import io.ktor.application.ApplicationCall
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.response.respondText
import mu.KLogging

abstract class Controller {
    companion object : KLogging()

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

    suspend inline fun ApplicationCall.respondJson(obj: Any, statusCode: Int) =
        respondText(gson.toJson(obj), ContentType.Application.Json, HttpStatusCode.fromValue(statusCode))
}
