package ie.setu.domain.db

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.jodatime.datetime

object CalorieLogs : Table("calorie_log") {
    val id = integer("id").autoIncrement() // Unique identifier for the calorie record
    val userId = integer("user_id").references(Users.id, onDelete = ReferenceOption.CASCADE) // Reference to user table ID
    val calories = integer("calories") // Amount of calories consumed daily
    val recordedAt = datetime("recorded_at") // Date and time of the record, format "YYYY-MM-DD HH:MM:SS"

    override val primaryKey = PrimaryKey(CalorieLogs.id, name = "PK_CalorieLog_ID")

}
