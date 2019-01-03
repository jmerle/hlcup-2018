package com.jaspervanmerle.hlcup2018.database

import mu.KLogging
import java.io.File
import java.sql.Connection
import java.sql.DriverManager

object Database : KLogging() {
    val connection = createConnection()

    fun init() {
        logger.info("Initializing database")

        connection.run {
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

                it.executeUpdate("CREATE INDEX interests_interest_idx ON interests (interest)")

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

                it.executeUpdate("CREATE INDEX likes_from_id_idx ON likes (from_id)")
                it.executeUpdate("CREATE INDEX likes_to_id_idx ON likes (to_id)")
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
