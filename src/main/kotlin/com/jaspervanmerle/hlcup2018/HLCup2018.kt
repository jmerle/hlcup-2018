package com.jaspervanmerle.hlcup2018

import com.jaspervanmerle.hlcup2018.controller.*
import com.jaspervanmerle.hlcup2018.database.Database
import com.jaspervanmerle.hlcup2018.model.DataFile
import io.ktor.application.call
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.netty.util.NettyRuntime
import mu.KLogging
import java.io.File
import java.sql.Types

object HLCup2018 : KLogging() {
    fun prepare(dataDirectory: File) {
        logger.info("Preparing using data files inside ${dataDirectory.absolutePath}")

        Database.init()

        for (file in dataDirectory.listFiles().sorted()) {
            processFile(file)
        }
    }

    fun processFile(file: File) {
        logger.info("Processing file ${file.absolutePath}")

        val data = gson.fromJson<DataFile>(file.readText(), DataFile::class.java)

        Database.connection.run {
            createStatement().use {
                it.execute("PRAGMA foreign_keys = OFF")
            }

            autoCommit = false

            prepareStatement("INSERT INTO accounts (id, email, first_name, last_name, phone, gender, birth, country, city, joined, status, premium_start, premium_end) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)").use {
                for (account in data.accounts) {
                    var index = 1

                    it.setInt(index++, account.id)
                    it.setString(index++, account.email)

                    if (account.firstName != null) {
                        it.setString(index++, account.firstName)
                    } else {
                        it.setNull(index++, Types.NULL)
                    }

                    if (account.lastName != null) {
                        it.setString(index++, account.lastName)
                    } else {
                        it.setNull(index++, Types.NULL)
                    }

                    if (account.phone != null) {
                        it.setString(index++, account.phone)
                    } else {
                        it.setNull(index++, Types.NULL)
                    }

                    it.setString(index++, account.gender)
                    it.setInt(index++, account.birth)

                    if (account.country != null) {
                        it.setString(index++, account.country)
                    } else {
                        it.setNull(index++, Types.NULL)
                    }

                    if (account.city != null) {
                        it.setString(index++, account.city)
                    } else {
                        it.setNull(index++, Types.NULL)
                    }

                    it.setInt(index++, account.joined)
                    it.setString(index++, account.status)

                    if (account.premium != null) {
                        it.setInt(index++, account.premium.start)
                        it.setInt(index, account.premium.end)
                    } else {
                        it.setNull(index++, Types.NULL)
                        it.setNull(index, Types.NULL)
                    }

                    it.addBatch()
                }

                it.executeBatch()
            }

            prepareStatement("INSERT INTO interests (account_id, interest) VALUES (?, ?)").use {
                for (account in data.accounts) {
                    if (account.interests != null) {
                        for (interest in account.interests) {
                            var index = 1

                            it.setInt(index++, account.id)
                            it.setString(index, interest)

                            it.addBatch()
                        }
                    }
                }

                it.executeBatch()
            }

            prepareStatement("INSERT INTO likes (from_id, to_id, timestamp) VALUES (?, ?, ?)").use {
                for (account in data.accounts) {
                    if (account.likes != null) {
                        for (like in account.likes) {
                            var index = 1

                            it.setInt(index++, account.id)
                            it.setInt(index++, like.toId)
                            it.setInt(index, like.timestamp)

                            it.addBatch()
                        }
                    }
                }

                it.executeBatch()
            }

            commit()
            autoCommit = false

            createStatement().use {
                it.execute("PRAGMA foreign_keys = ON")
            }
        }
    }

    fun run(port: Int) {
        logger.info("Starting server on port $port")

        if (System.getenv("DOCKER") != null) {
            // By default Netty reports 0 available processors on the shelling system
            NettyRuntime.setAvailableProcessors(4)
        }

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
