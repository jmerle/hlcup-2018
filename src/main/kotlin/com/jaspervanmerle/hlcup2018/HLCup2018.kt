package com.jaspervanmerle.hlcup2018

import com.jaspervanmerle.hlcup2018.controller.*
import io.ktor.application.call
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.netty.util.NettyRuntime
import mu.KLogging
import java.io.File

object HLCup2018 : KLogging() {
    fun prepare(dataFile: File) {
        logger.info("Preparing using data archive at ${dataFile.absolutePath}")
    }

    fun run(port: Int) {
        logger.info("Starting server on port $port")

        NettyRuntime.setAvailableProcessors(4)

        embeddedServer(Netty, port) {
            routing {
                get("/accounts/filter/") {
                    FilterAccountsController.process(call)
                }

                get("/accounts/group/") {
                    GroupAccountsController.process(call)
                }

                get("/accounts/{id}/recommend/") {
                    RecommendAccountsController.process(call)
                }

                get("/accounts/{id}/suggest/") {
                    SuggestAccountsController.process(call)
                }

                post("/accounts/new/") {
                    CreateAccountController.process(call)
                }

                post("/accounts/{id}/") {
                    UpdateAccountController.process(call)
                }

                post("/accounts/likes/") {
                    CreateLikesController.process(call)
                }
            }
        }.start(wait = true)
    }
}
