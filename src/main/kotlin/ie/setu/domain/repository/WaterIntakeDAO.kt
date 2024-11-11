package ie.setu.domain.repository

import ie.setu.domain.WaterIntake
import ie.setu.domain.db.Users
import ie.setu.domain.db.WaterIntakes
import ie.setu.utils.mapToWaterIntake
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.jetbrains.exposed.sql.deleteWhere



class WaterIntakeDAO {
    // Find water intake records by user ID
    fun findByUserId(userId: Int): List<WaterIntake> = transaction {
        WaterIntakes.selectAll().where { WaterIntakes.userId eq userId }
            .map { mapToWaterIntake(it) }
    }

    /**
     * Find water intake records by user ID and date range
     * start: current day 00:00
     * end: the day after current day 00:00
     * */
    fun findByUserIdAndDateRange(userId: Int, start: DateTime, end: DateTime): List<WaterIntake> = transaction {
        // Check if the user exists
        if (Users.selectAll().where { Users.id eq userId }.empty()) {
            return@transaction emptyList()
        }

        // Validate the date range
        if (start > end) {
            throw IllegalArgumentException("Start date must be before end date.")
        }

        // Query the water intakes based on the user ID and date range
        WaterIntakes.selectAll().where {
            (WaterIntakes.userId eq userId) and
                    (WaterIntakes.recordedAt greaterEq start) and
                    (WaterIntakes.recordedAt lessEq end)
        }.map { mapToWaterIntake(it) }
    }


    // Add a new water intake record
    fun add(waterIntake: WaterIntake) = transaction {
        if (waterIntake.amount <= 0) {
            throw IllegalArgumentException("Water intake amount must be greater than zero.")
        }

        WaterIntakes.insert {
            it[amount] = waterIntake.amount
            it[recordedAt] = waterIntake.recordedAt
            it[userId] = waterIntake.userId
        }
    }

    // Delete a water intake record by water intake ID
    fun deleteByWaterIntakeId(waterIntakeId: Int): Boolean = transaction {
        val deletedCount = WaterIntakes.deleteWhere { WaterIntakes.id eq waterIntakeId }
        deletedCount > 0
    }

    // Delete all water intake records for a specific user
    fun deleteByUserId(userId: Int): Boolean = transaction {
        val deletedCount = WaterIntakes.deleteWhere { WaterIntakes.userId eq userId }
        deletedCount > 0
    }

}