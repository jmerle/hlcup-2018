package com.jaspervanmerle.hlcup2018

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.File

val gson: Gson = GsonBuilder().create()

fun main(args: Array<String>) {
    val dataDirectory = File(System.getenv("HLCUP_2018_DATA_PATH") ?: "data/")
    val port = if (System.getenv("DOCKER") != null) 80 else 8080

    HLCup2018.prepare(dataDirectory)
    HLCup2018.run(port)
}
