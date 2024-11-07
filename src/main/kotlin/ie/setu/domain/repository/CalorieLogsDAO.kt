package ie.setu.domain.repository

import ie.setu.domain.db.CalorieLogs
import ie.setu.domain.CalorieLog
import ie.setu.domain.db.Users
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime


class CalorieLogsDAO {

    /**
     * Find calorie records for a specific user
     * If start and end dates are provided, filter by date range
     */
    fun findByUserId(userId: Int, start: DateTime? = null, end: DateTime? = null): List<CalorieLog> = transaction {
        // Check if the user exists
        if (Users.selectAll().where { Users.id eq userId }.empty()) {
            return@transaction emptyList()
        }

        // Validate the date range if provided
        if (start != null && end != null && start > end) {
            throw IllegalArgumentException("Start date must be before end date.")
        }

        // Query calorie records based on the provided parameters
        CalorieLogs.selectAll().where {
            (CalorieLogs.userId eq userId) and
                    (start?.let { CalorieLogs.recordedAt greaterEq it } ?: Op.TRUE) and
                    (end?.let { CalorieLogs.recordedAt lessEq it } ?: Op.TRUE)
        }.map { mapToCalorieLog(it) }
    }

    // Add a new calorie record
    fun add(calorieLog: CalorieLog) = transaction {
        if (calorieLog.calories <= 0) {
            throw IllegalArgumentException("Calories must be greater than zero.")
        }

        CalorieLogs.insert {
            it[userId] = calorieLog.userId
            it[calories] = calorieLog.calories
            it[recordedAt] = calorieLog.recordedAt
        }
    }

    // Map the query result to a CalorieLog object
    private fun mapToCalorieLog(row: ResultRow): CalorieLog = CalorieLog(
        id = row[CalorieLogs.id],
        userId = row[CalorieLogs.userId],
        calories = row[CalorieLogs.calories],
        recordedAt = row[CalorieLogs.recordedAt] // Can be converted to LocalDateTime
    )

}