package ie.setu.controllers

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
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
            val objectMapper = jacksonObjectMapper()
            val waterIntakeMap: Map<String, Any> = objectMapper.readValue(ctx.body())

            val userId = waterIntakeMap["user-id"] as? Int ?: run {
                ctx.status(400).result("Invalid user ID format")
                return
            }

            val calories = waterIntakeMap["calories"] as? Int ?: run {
                ctx.status(400).result("Invalid calories format")
                return
            }

            val dateStr = waterIntakeMap["recorded-at"] as? String ?: run {
                ctx.status(400).result("Invalid user ID format")
                return
            }

            val recordedAt = DateTime.parse(dateStr) // Parsing ISO 8601 format string

            val newCalorieLog = CalorieLog(userId = userId, calories = calories, recordedAt = recordedAt)
            calorieLogDAO.add(newCalorieLog)

            ctx.status(201).result("Calorie record added successfully")
        } catch (e: Exception) {
            ctx.status(500).result("Internal server error")
        }
    }

    // Find a calorie log by calorie log ID
    fun findByCalorieLogId(ctx: Context) {
        try {
            val calorieLogId = ctx.pathParam("calorie-log-id").toIntOrNull() ?: run {
                ctx.status(400).result("Invalid calorie log ID format")
                return
            }

            val calorieLog = calorieLogDAO.findByCalorieLogId(calorieLogId)
            if (calorieLog != null) {
                ctx.json(calorieLog)
            } else {
                ctx.status(404).result("Calorie log not found")
            }
        } catch (e: Exception) {
            ctx.status(500).result("Internal server error")
        }
    }

    // Delete all calorie logs for a specific user by user ID
    fun deleteByUserId(ctx: Context) {
        try {
            val userId = ctx.pathParam("user-id").toIntOrNull() ?: run {
                ctx.status(400).result("Invalid user ID format")
                return
            }

            val deleted = calorieLogDAO.deleteByUserId(userId)
            if (deleted) {
                ctx.status(200).result("All calorie logs for the user deleted successfully")
            } else {
                ctx.status(404).result("No calorie logs found for the user")
            }
        } catch (e: Exception) {
            ctx.status(500).result("Internal server error")
        }
    }

    // Delete a calorie log by calorie log ID
    fun deleteByCalorieLogId(ctx: Context) {
        try {
            val calorieLogId = ctx.pathParam("calorie-log-id").toIntOrNull() ?: run {
                ctx.status(400).result("Invalid calorie log ID format")
                return
            }

            val deleted = calorieLogDAO.deleteByCalorieLogId(calorieLogId)
            if (deleted) {
                ctx.status(200).result("Calorie log deleted successfully")
            } else {
                ctx.status(404).result("Calorie log not found")
            }
        } catch (e: Exception) {
            ctx.status(500).result("Internal server error")
        }
    }


}