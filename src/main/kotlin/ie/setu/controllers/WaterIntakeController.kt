package ie.setu.controllers

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import ie.setu.domain.repository.WaterIntakeDAO
import ie.setu.domain.WaterIntake
import io.javalin.http.Context
import org.joda.time.DateTime

class WaterIntakeController() {

    private val waterIntakeDAO = WaterIntakeDAO()

    //--------------------------------------------------------------
    // waterIntakeDAO specifics
    //-------------------------------------------------------------
    // Get all water intake records for a specific user
    fun getWaterIntakeByUserId(ctx: Context) {
        // Extract user ID from the request path
        try {
            val userId = ctx.pathParam("user-id").toIntOrNull() ?: run {
                ctx.status(400).result("Invalid user ID format")
                return
            }

            val waterIntakes = waterIntakeDAO.findByUserId(userId)

            if (waterIntakes.isNotEmpty()) {
                val totalWaterIntake = waterIntakes.sumOf { it.amount }

                val mapper = jacksonObjectMapper()
                    .registerModule(JodaModule())
                    .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)

                val response = mapOf(
                    "totalWaterIntake" to totalWaterIntake,
                    "waterIntakes" to waterIntakes
                )

                ctx.json(mapper.writeValueAsString(response))
            } else {
                ctx.status(404).result("No water intake records found for the user")
            }
        } catch (e: Exception) {
            ctx.status(500).result("Internal server error")
        }
    }

    // Find a water intake record by its ID
    fun getWaterIntakeById(ctx: Context) {
        try {
            val waterIntakeId = ctx.pathParam("waterintake-id").toIntOrNull() ?: run {
                ctx.status(400).result("Invalid water intake ID format")
                return
            }

            val waterIntake = waterIntakeDAO.findByWaterIntakeId(waterIntakeId)
            if (waterIntake != null) {
                ctx.json(waterIntake)
            } else {
                ctx.status(404).result("Water intake record not found")
            }
        } catch (e: Exception) {
            ctx.status(500).result("Internal server error")
        }
    }

    // Add a new water intake record for a specific user
    fun addWaterIntake(ctx: Context) {
        // Add new water intake record
        try {
            val userId = ctx.queryParam("user-id")?.toIntOrNull() ?: run {
                ctx.status(400).result("Invalid user ID format")
                return
            }
            val amount = ctx.queryParam("amount")?.toDoubleOrNull() ?: run {
                ctx.status(400).result("Invalid water amount format")
                return
            }

            val recordedAtString = ctx.queryParam("recorded-at") ?: run {
                ctx.status(400).result("Invalid recorded-at format")
                return
            }

            val recordedAt = DateTime.parse(recordedAtString) // Parsing ISO 8601 format string

            val newWaterIntake = WaterIntake(userId = userId, amount = amount, recordedAt = recordedAt)
            waterIntakeDAO.add(newWaterIntake)

            ctx.status(201).result("Water intake record added successfully")
        } catch (e: Exception) {
            ctx.status(500).result("Internal server error")
        }
    }


    // Get the summary of today's water intake for a specific user
    fun getDailyWaterIntakeSummary(ctx: Context) {
        try {
            val userId = ctx.pathParam("user-id").toIntOrNull() ?: run {
                ctx.status(400).result("Invalid user ID format")
                return
            }
            val today = DateTime.now().withTimeAtStartOfDay()
            val tomorrow = today.plusDays(1)
            /**
             * today: current day, start at 00:00
             * tomorrow: current day + 1, start at 00:00
             * */
            val waterIntakes = waterIntakeDAO.findByUserIdAndDateRange(userId, today, tomorrow)

            if (waterIntakes.isNotEmpty()) {
                val totalWaterIntake = waterIntakes.sumOf { it.amount }
                val mapper = jacksonObjectMapper()
                    .registerModule(JodaModule())
                    .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)

                val response = mapOf(
                    "date" to today.toString("yyyy-MM-dd"),
                    "totalWaterIntake" to totalWaterIntake,
                    "waterIntakes" to waterIntakes
                )

                ctx.json(mapper.writeValueAsString(response))
            } else {
                ctx.status(404).result("No water intake records found for today")
            }
        } catch (e: Exception) {
            ctx.status(500).result("Internal server error")
        }
    }

    // Delete a water intake record by water intake ID
    fun deleteWaterIntakesByWaterIntakeId(ctx: Context) {
        try {
            val waterIntakeId = ctx.pathParam("waterintakes-id").toIntOrNull() ?: run {
                ctx.status(400).result("Invalid water intake ID format")
                return
            }

            val deleted = waterIntakeDAO.deleteByWaterIntakeId(waterIntakeId)
            if (deleted) {
                ctx.status(200).result("Water intake record deleted successfully")
            } else {
                ctx.status(404).result("Water intake record not found")
            }
        } catch (e: Exception) {
            ctx.status(500).result("Internal server error")
        }
    }

    // Delete all water intake records for a specific user
    fun deleteWaterIntakeByUserId(ctx: Context) {
        try {
            val userId = ctx.pathParam("user-id").toIntOrNull() ?: run {
                ctx.status(400).result("Invalid user ID format")
                return
            }

            val deleted = waterIntakeDAO.deleteByUserId(userId)
            if (deleted) {
                ctx.status(200).result("All water intake records for the user deleted successfully")
            } else {
                ctx.status(404).result("No water intake records found for the user")
            }
        } catch (e: Exception) {
            ctx.status(500).result("Internal server error")
        }
    }


}