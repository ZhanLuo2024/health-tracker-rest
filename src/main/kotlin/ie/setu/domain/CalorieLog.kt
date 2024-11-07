package ie.setu.domain

import org.joda.time.DateTime

data class CalorieLog(
    var id: Int = 0,            // CalorieLog ID
    var calories: Int,          // Amount of calories consumed daily
    var recordedAt: DateTime,         // Date and time of the record
    var userId: Int             // Reference to the user table ID
)