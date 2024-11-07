package ie.setu.controllers

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import ie.setu.domain.CalorieLog
import ie.setu.domain.repository.CalorieLogsDAO
import io.javalin.http.Context
import org.joda.time.DateTime


class CalorieLogController {
    private val calorieLogDAO = CalorieLogsDAO()


    fun getCalorieLogByUserId(ctx: Context) {
        try {
            val userId = ctx.pathParam("user-id").toIntOrNull() ?: run {
                ctx.status(400).result("Invalid user ID format")
                return
            }

            val start = ctx.queryParam("start")?.let { DateTime.parse(it) }
            val end = ctx.queryParam("end")?.let { DateTime.parse(it) }

            val calorieLogs = calorieLogDAO.findByUserId(userId, start, end)

            if (calorieLogs.isNotEmpty()) {
                val totalCalories = calorieLogs.sumOf { it.calories }

                val mapper = jacksonObjectMapper()
                    .registerModule(JodaModule())
                    .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)

                val response = mapOf(
                    "totalCalories" to totalCalories,
                    "calorieLogs" to calorieLogs
                )

                ctx.json(mapper.writeValueAsString(response))
            } else {
                ctx.status(404).result("No calorie records found for the user")
            }
        } catch (e: Exception) {
            ctx.status(500).result("Internal server error")
        }
    }



    // Add a new calorie record for a specific user
    fun addCalorieLog(ctx: Context) {
        try {
            val userId = ctx.queryParam("user-id")?.toIntOrNull() ?: run {
                ctx.status(400).result("Invalid user ID format")
                return
            }
            val calories = ctx.queryParam("calories")?.toIntOrNull() ?: run {
                ctx.status(400).result("Invalid calories format")
                return
            }

            val recordedAtString = ctx.queryParam("recorded-at") ?: run {
                ctx.status(400).result("Invalid recorded-at format")
                return
            }

            val recordedAt = DateTime.parse(recordedAtString) // Parsing ISO 8601 format string

            val newCalorieLog = CalorieLog(userId = userId, calories = calories, recordedAt = recordedAt)
            calorieLogDAO.add(newCalorieLog)

            ctx.status(201).result("Calorie record added successfully")
        } catch (e: Exception) {
            ctx.status(500).result("Internal server error")
        }
    }


}