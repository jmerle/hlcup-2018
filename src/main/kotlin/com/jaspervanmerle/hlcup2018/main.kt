package com.jaspervanmerle.hlcup2018

import java.io.File

fun main(args: Array<String>) {
    val dataFile = File(System.getenv("HLCUP_2018_DATA_PATH") ?: "/tmp/data/data.zip")
    val port = if (System.getenv("DOCKER") != null) 80 else 8080

    HLCup2018.prepare(dataFile)
    HLCup2018.run(port)
}
