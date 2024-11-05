package ie.setu.domain

import org.joda.time.DateTime

data class WaterIntake(
    var id: Int,
    var amount: Double, // Amount of water in liters
    var recordedAt: DateTime,
    var userId: Int
)