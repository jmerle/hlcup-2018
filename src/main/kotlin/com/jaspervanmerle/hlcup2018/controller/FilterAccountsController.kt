package com.jaspervanmerle.hlcup2018.controller

import com.jaspervanmerle.hlcup2018.database.Database
import com.jaspervanmerle.hlcup2018.model.response.FilterAccountsAccount
import com.jaspervanmerle.hlcup2018.model.response.FilterAccountsPremium
import com.jaspervanmerle.hlcup2018.model.response.FilterAccountsResponse
import io.ktor.application.ApplicationCall
import org.sqlite.SQLiteException

object FilterAccountsController : Controller() {
    init {
        validParameters += arrayOf(
            "sex_eq",
            "email_domain", "email_lt", "email_gt",
            "status_eq", "status_neq",
            "fname_eq", "fname_any", "fname_null",
            "sname_eq", "sname_starts", "sname_null",
            "phone_code", "phone_null",
            "country_eq", "country_null",
            "city_eq", "city_any", "city_null",
            "birth_lt", "birth_gt", "birth_year",
            "interests_contains", "interests_any",
            "likes_contains",
            "premium_now", "premium_null",
            "limit"
        )
    }

    override suspend fun processCall(call: ApplicationCall) {
        val fields: MutableSet<String> = mutableSetOf("id", "email")
        val checks: MutableList<String> = mutableListOf()

        val params = call.request.queryParameters

        for (name in params.names()) {
            when (name) {
                "sex_eq" -> {
                    fields += "gender"
                    checks += "gender = '${params["sex_eq"]!!.escapeSql()}'"
                }
                "email_domain" -> {
                    fields += "email"
                    checks += "email LIKE '%@${params["email_domain"]!!.escapeSql()}'"
                }
                "email_lt" -> {
                    fields += "email"
                    checks += "email < '${params["email_lt"]!!.escapeSql()}'"
                }
                "email_gt" -> {
                    fields += "email"
                    checks += "email > '${params["email_gt"]!!.escapeSql()}'"
                }
                "status_eq" -> {
                    fields += "status"
                    checks += "status = '${params["status_eq"]!!.escapeSql()}'"
                }
                "status_neq" -> {
                    fields += "status"
                    checks += "status != '${params["status_neq"]!!.escapeSql()}'"
                }
                "fname_eq" -> {
                    fields += "first_name"
                    checks += "first_name = '${params["fname_eq"]!!.escapeSql()}'"
                }
                "fname_any" -> {
                    fields += "first_name"
                    checks += "first_name IN (${params["fname_any"]!!.escapeSqlList()})"
                }
                "fname_null" -> {
                    fields += "first_name"
                    checks += "first_name IS ${if (params["fname_null"] == "0") "NOT " else ""}NULL"
                }
                "sname_eq" -> {
                    fields += "last_name"
                    checks += "last_name = '${params["sname_eq"]!!.escapeSql()}'"
                }
                "sname_starts" -> {
                    fields += "last_name"
                    checks += "last_name LIKE '${params["sname_starts"]!!.escapeSql()}%'"
                }
                "sname_null" -> {
                    fields += "last_name"
                    checks += "last_name IS ${if (params["sname_null"] == "0") "NOT " else ""}NULL"
                }
                "phone_code" -> {
                    fields += "phone"
                    checks += "phone LIKE '%(${params["phone_code"]!!.escapeSql()})%'"
                }
                "phone_null" -> {
                    fields += "phone"
                    checks += "phone IS ${if (params["phone_null"] == "0") "NOT " else ""}NULL"
                }
                "country_eq" -> {
                    fields += "country"
                    checks += "country = '${params["country_eq"]!!.escapeSql()}'"
                }
                "country_null" -> {
                    fields += "country"
                    checks += "country IS ${if (params["country_null"] == "0") "NOT " else ""}NULL"
                }
                "city_eq" -> {
                    fields += "city"
                    checks += "city = '${params["city_eq"]!!.escapeSql()}'"
                }
                "city_any" -> {
                    fields += "city"
                    checks += "city IN (${params["city_any"]!!.escapeSqlList()})"
                }
                "city_null" -> {
                    fields += "city"
                    checks += "city IS ${if (params["city_null"] == "0") "NOT " else ""}NULL"
                }
                "birth_lt" -> {
                    fields += "birth"
                    checks += "birth < ${params["birth_lt"]!!.escapeSql()}"
                }
                "birth_gt" -> {
                    fields += "birth"
                    checks += "birth > ${params["birth_gt"]!!.escapeSql()}"
                }
                "birth_year" -> {
                    fields += "birth"
                    checks += "STRFTIME('%Y', DATETIME(birth, 'unixepoch')) = '${params["birth_year"]!!.escapeSql()}'"
                }
                "interests_contains" -> {
                    val interests = params["interests_contains"]!!.split(",")
                    val or = interests.joinToString(" OR ") { "interest = '${it.escapeSql()}'" }

                    checks += "id IN (SELECT account_id FROM interests WHERE $or GROUP BY account_id HAVING COUNT(account_id) = ${interests.size})"
                }
                "interests_any" -> {
                    checks += "id IN (SELECT DISTINCT account_id FROM interests WHERE interest IN (${params["interests_any"]!!.escapeSqlList()}))"
                }
                "likes_contains" -> {
                    val ids = params["likes_contains"]!!.split(",")
                    val or = ids.joinToString(" OR ") { "to_id = '${it.escapeSql()}'" }

                    checks += "id IN (SELECT from_id FROM likes WHERE $or GROUP BY from_id HAVING COUNT(from_id) = ${ids.size})"
                }
                "premium_now" -> {
                    fields += "premium_start"
                    fields += "premium_end"
                    checks += "DATETIME(premium_start, 'unixepoch') <= DATETIME('now') AND DATETIME(premium_end, 'unixepoch') >= DATETIME('now')"
                }
                "premium_null" -> {
                    checks += "premium_start IS ${if (params["premium_null"] == "0") "NOT " else ""}NULL"
                }
            }
        }

        val accounts: MutableList<FilterAccountsAccount> = mutableListOf()
        val limit = params["limit"]

        if (limit != null && limit.toIntOrNull() != null) {
            Database.connection.run {
                createStatement().use {
                    val cols = fields.joinToString(", ")
                    val and = checks.joinToString(" AND ")
                    val where = if (checks.size > 0) "WHERE $and" else ""

                    val query =
                        "SELECT $cols FROM accounts $where ORDER BY id DESC LIMIT $limit"

                    try {
                        it.executeQuery(query).use { rs ->
                            while (rs.next()) {
                                val id = rs.getInt("id")
                                val email = rs.getString("email")

                                val account = FilterAccountsAccount(id, email)

                                for (field in fields) {
                                    when (field) {
                                        "first_name" -> account.firstName = rs.getString("first_name")
                                        "last_name" -> account.lastName = rs.getString("last_name")
                                        "phone" -> account.phone = rs.getString("phone")
                                        "gender" -> account.gender = rs.getString("gender")
                                        "birth" -> account.birth = rs.getInt("birth")
                                        "country" -> account.country = rs.getString("country")
                                        "city" -> account.city = rs.getString("city")
                                        "status" -> account.status = rs.getString("status")
                                        "premium_start" -> account.premium =
                                                FilterAccountsPremium(
                                                    rs.getInt("premium_start"),
                                                    rs.getInt("premium_end")
                                                )
                                    }
                                }

                                accounts += account
                            }
                        }
                    } catch (e: SQLiteException) {
                        logger.warn("Invalid query: $query")
                    }
                }
            }
        }

        call.respondJson(FilterAccountsResponse(accounts), 200)
    }
}
