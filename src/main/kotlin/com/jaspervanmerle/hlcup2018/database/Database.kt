package com.jaspervanmerle.hlcup2018.database

import com.jaspervanmerle.hlcup2018.lock.LockedObject
import mu.KLogging
import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import java.util.concurrent.locks.ReentrantLock

object Database : KLogging() {
    val connection = LockedObject(ReentrantLock(), createConnection())

    fun init() {
        logger.info("Initializing database")

        connection.runWithLock {
            createStatement().use {
                it.execute("PRAGMA foreign_keys = ON")

                it.executeUpdate(
                    """
                    CREATE TABLE accounts (
                        id INT NOT NULL PRIMARY KEY,
                        email VARCHAR(50) NOT NULL UNIQUE,
                        first_name VARCHAR(50),
                        last_name VARCHAR(50),
                        phone VARCHAR(16),
                        gender CHAR NOT NULL,
                        birth INT NOT NULL,
                        birth_year INT NOT NULL,
                        country VARCHAR(50),
                        city VARCHAR(50),
                        joined INT NOT NULL,
                        status VARCHAR(10) NOT NULL,
                        premium_start INT,
                        premium_end INT
                    )
                """.trimIndent()
                )

                it.executeUpdate(
                    """
                    CREATE TABLE interests (
                        account_id INT NOT NULL,
                        interest VARCHAR(100) NOT NULL,
                        PRIMARY KEY (account_id, interest),
                        FOREIGN KEY (account_id) REFERENCES accounts (id)
                    )
                """.trimIndent()
                )

                it.executeUpdate(
                    """
                    CREATE TABLE likes (
                        from_id INT NOT NULL,
                        to_id INT NOT NULL,
                        timestamp INT NOT NULL,
                        FOREIGN KEY (from_id) REFERENCES accounts (id),
                        FOREIGN KEY (to_id) REFERENCES accounts (id)
                    )
                """.trimIndent()
                )
            }
        }
    }

    private fun createConnection(): Connection {
        val databaseFile = File("database.db")

        if (databaseFile.exists()) {
            logger.info("Database already exists, recreating ${databaseFile.absolutePath}")
            databaseFile.delete()
        } else {
            logger.info("Creating database at ${databaseFile.absolutePath}")
            databaseFile.createNewFile()
        }

        Class.forName("org.sqlite.JDBC")
        return DriverManager.getConnection("jdbc:sqlite:${databaseFile.absolutePath}")
    }
}
