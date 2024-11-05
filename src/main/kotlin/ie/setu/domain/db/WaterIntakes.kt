package ie.setu.domain.db

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.jodatime.datetime

object WaterIntakes : Table("water_intakes") {
    val id = integer("id").autoIncrement()
    val amount = double("amount") // Amount of water in liters
    val recordedAt = datetime("recorded_at")
    val userId = integer("user_id").references(Users.id, onDelete = ReferenceOption.CASCADE)

    override val primaryKey = PrimaryKey(WaterIntakes.id, name = "PK_WaterIntakes_ID")
}