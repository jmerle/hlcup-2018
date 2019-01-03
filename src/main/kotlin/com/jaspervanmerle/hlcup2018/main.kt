package com.jaspervanmerle.hlcup2018

import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.joran.JoranConfigurator
import ch.qos.logback.core.joran.spi.JoranException
import ch.qos.logback.core.util.StatusPrinter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.slf4j.LoggerFactory
import java.io.File

val gson: Gson = GsonBuilder().create()

fun main(args: Array<String>) {
    val context = LoggerFactory.getILoggerFactory() as LoggerContext
    val configFile = "/logback-${System.getenv("ENVIRONMENT") ?: "prod"}.xml"

    try {
        val configurator = JoranConfigurator()
        configurator.context = context
        context.reset()
        configurator.doConfigure(HLCup2018.javaClass.getResource(configFile))
    } catch (e: JoranException) {
        //
    }

    StatusPrinter.printInCaseOfErrorsOrWarnings(context)

    val dataDirectory = File(System.getenv("HLCUP_2018_DATA_PATH") ?: "data/")
    val port = if (System.getenv("DOCKER") != null) 80 else 8080

    HLCup2018.prepare(dataDirectory)
    HLCup2018.run(port)
}
