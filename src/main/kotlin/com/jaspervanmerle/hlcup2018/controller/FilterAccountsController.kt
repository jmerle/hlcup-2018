package com.jaspervanmerle.hlcup2018.controller

import com.jaspervanmerle.hlcup2018.database.Database
import com.jaspervanmerle.hlcup2018.model.response.FilterAccountsAccount
import com.jaspervanmerle.hlcup2018.model.response.FilterAccountsResponse
import io.ktor.application.ApplicationCall

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
                    checks += "gender = '${params["sex_eq"]}'"
                }
                "email_domain" -> {
                    fields += "email"
                    checks += "email = '%@${params["email_domain"]}'"
                }
                "email_lt" -> {
                    fields += "email"
                    checks += "email < '${params["email_lt"]}'"
                }
                "email_gt" -> {
                    fields += "email"
                    checks += "email > '${params["email_gt"]}'"
                }
                "status_eq" -> {
                    fields += "status"
                    checks += "status = '${params["status_eq"]}'"
                }
                "status_neq" -> {
                    fields += "status"
                    checks += "status != '${params["status_neq"]}'"
                }
                "fname_eq" -> {
                    fields += "first_name"
                    checks += "first_name = '${params["fname_eq"]}'"
                }
                "fname_any" -> {
                    fields += "first_name"
                    checks += "first_name IN (${params["fname_any"]!!.split(",").joinToString(",") { "'$it'" }})"
                }
                "fname_null" -> {
                    fields += "first_name"
                    checks += "first_name IS ${if (params["fname_null"] == "0") "NOT " else ""}NULL"
                }
                "sname_eq" -> {
                    fields += "last_name"
                    checks += "last_name = '${params["sname_eq"]}'"
                }
                "sname_starts" -> {
                    fields += "last_name"
                    checks += "last_name LIKE '${params["sname_starts"]}%'"
                }
                "sname_null" -> {
                    fields += "last_name"
                    checks += "last_name IS ${if (params["sname_null"] == "0") "NOT " else ""}NULL"
                }
                "phone_code" -> {
                    fields += "phone"
                    checks += "phone LIKE '%(${params["phone_code"]})%'"
                }
                "phone_null" -> {
                    fields += "phone"
                    checks += "phone IS ${if (params["phone_null"] == "0") "NOT " else ""}NULL"
                }
                "country_eq" -> {
                    fields += "country"
                    checks += "country = '${params["country_eq"]}'"
                }
                "country_null" -> {
                    fields += "country"
                    checks += "country IS ${if (params["country_null"] == "0") "NOT " else ""}NULL"
                }
                "city_eq" -> {
                    fields += "city"
                    checks += "city = '${params["city_eq"]}'"
                }
                "city_any" -> {
                    fields += "city"
                    checks += "city IN (${params["city_eq"]!!.split(",").joinToString(",") { "'$it'" }})"
                }
                "city_null" -> {
                    fields += "city"
                    checks += "city IS ${if (params["city_null"] == "0") "NOT " else ""}NULL"
                }
                "birth_lt" -> {
                    fields += "birth"
                    checks += "birth < ${params["birth_lt"]}"
                }
                "birth_gt" -> {
                    fields += "birth"
                    checks += "birth > ${params["birth_gt"]}"
                }
                "birth_year" -> {
                    fields += "birth"
                    checks += "birth_year = ${params["birth_year"]}"
                }
                "interests_contains" -> {
                    checks += "TODO"
                }
                "interests_any" -> {
                    checks += "TODO"
                }
                "likes_contains" -> {
                    checks += "TODO"
                }
                "premium_now" -> {
                    fields += "premium"
                    checks += "premium_start >= DATE('now') AND premium_end <= DATE('now')"
                }
                "premium_null" -> {
                    fields += "premium"
                    checks += "premium_start IS ${if (params["premium_null"] == "0") "NOT " else ""}NULL"
                }
            }
        }

        val accounts: MutableList<FilterAccountsAccount> = mutableListOf()

        Database.connection.runWithLock {
            createStatement().use {
                val query =
                    "SELECT ${fields.joinToString(", ")} FROM accounts ${if (checks.size > 0) "WHERE ${checks.joinToString(
                        " AND "
                    )}" else ""} LIMIT ${params["limit"]}"

                it.executeQuery(query).use { rs ->
                    while (rs.next()) {
                        val id = rs.getInt("id")
                        val email = rs.getString("email")

                        val account = FilterAccountsAccount(id, email)

                        for (field in fields) {
                            when (field) {
                                "first_name" -> account.firstName = rs.getString("first_name")
                                "last_name" -> account.lastName = rs.getString("last_name")
                                "phone" -> account.phone = rs.getString("first_name")
                                "gender" -> account.gender = rs.getString("first_name")
                                "birth" -> account.birth = rs.getInt("birth")
                                "country" -> account.country = rs.getString("country")
                                "city" -> account.city = rs.getString("city")
                                "status" -> account.status = rs.getString("status")
                            }
                        }

                        accounts += account
                    }
                }
            }
        }

        call.respondJson(FilterAccountsResponse(accounts), 200)
    }
}
