package ie.setu.utils

import ie.setu.domain.User
import ie.setu.domain.db.Users
import org.jetbrains.exposed.sql.ResultRow
import ie.setu.domain.Activity
import ie.setu.domain.CalorieLog
import ie.setu.domain.db.Activities
import ie.setu.domain.db.WaterIntakes
import ie.setu.domain.WaterIntake
import ie.setu.domain.db.CalorieLogs

fun mapToUser(row: ResultRow): User = User(
    id = row[Users.id],
    name = row[Users.name],
    email = row[Users.email],
    password = row[Users.password],
    isLoggedIn = row[Users.isLoggedIn]
)

fun mapToActivity(it: ResultRow) = Activity(
    id = it[Activities.id],
    description = it[Activities.description],
    duration = it[Activities.duration],
    started = it[Activities.started],
    calories = it[Activities.calories],
    userId = it[Activities.userId]
)


fun mapToWaterIntake(it: ResultRow) = WaterIntake(
    id = it[WaterIntakes.id],
    amount = it[WaterIntakes.amount],
    recordedAt = it[WaterIntakes.recordedAt],
    userId = it[WaterIntakes.userId]
)

// Map the query result to a CalorieLog object
fun mapToCalorieLog(row: ResultRow): CalorieLog = CalorieLog(
    id = row[CalorieLogs.id],
    userId = row[CalorieLogs.userId],
    calories = row[CalorieLogs.calories],
    recordedAt = row[CalorieLogs.recordedAt] // Can be converted to LocalDateTime
)
