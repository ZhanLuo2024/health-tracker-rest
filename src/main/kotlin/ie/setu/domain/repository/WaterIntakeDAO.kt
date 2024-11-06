package ie.setu.domain.repository

import ie.setu.domain.WaterIntake
import ie.setu.domain.db.WaterIntakes
import ie.setu.utils.mapToWaterIntake
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime


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
        WaterIntakes.selectAll().where {
            (WaterIntakes.userId eq userId) and
                    (WaterIntakes.recordedAt greaterEq start) and
                    (WaterIntakes.recordedAt less end)
        }.map { mapToWaterIntake(it) }
    }

    // Add a new water intake record
    fun add(waterIntake: WaterIntake) = transaction {
        WaterIntakes.insert {
            it[amount] = waterIntake.amount
            it[recordedAt] = waterIntake.recordedAt
            it[userId] = waterIntake.userId
        }
    }
}