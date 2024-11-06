package ie.setu.controllers

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import ie.setu.domain.Activity
import ie.setu.domain.repository.ActivityDAO
import io.javalin.http.Context
import ie.setu.domain.repository.UserDAO

class ActivityController {

    private val userDao = UserDAO()
    private val activityDAO = ActivityDAO()

    //--------------------------------------------------------------
    // ActivityDAO specifics
    //-------------------------------------------------------------

    fun getAllActivities(ctx: Context) {
        //mapper handles the deserialization of Joda date into a String.
        val mapper = jacksonObjectMapper()
            .registerModule(JodaModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        ctx.json(mapper.writeValueAsString( activityDAO.getAll() ))
    }

    fun getActivitiesByUserId(ctx: Context) {
        if (userDao.findById(ctx.pathParam("user-id").toInt()) != null) {
            val activities = activityDAO.findByUserId(ctx.pathParam("user-id").toInt())
            if (activities.isNotEmpty()) {
                //mapper handles the deserialization of Joda date into a String.
                val mapper = jacksonObjectMapper()
                    .registerModule(JodaModule())
                    .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                ctx.json(mapper.writeValueAsString(activities))
            }
        }
    }

    fun addActivity(ctx: Context) {
        //mapper handles the serialisation of Joda date into a String.
        val mapper = jacksonObjectMapper()
            .registerModule(JodaModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        val activity = mapper.readValue<Activity>(ctx.body())
        activityDAO.save(activity)
        ctx.json(activity)
    }

    fun getActivityById(ctx: Context) {
        // Extracting the activity ID from the request path parameter named "activity-id"
        try {
            // Extracting the activity ID from the request path parameter named "activity-id"
            val activityId = ctx.pathParam("activity-id").toIntOrNull() ?: run {
                ctx.status(400).result("Invalid activity ID format")
                return
            }

            // Using the activityDAO to find the activity with the given ID
            val activity = activityDAO.findByActivityId(activityId)

            if (activity != null) {
                val mapper = jacksonObjectMapper()
                    .registerModule(JodaModule())
                    .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)

                // Respond with the serialized JSON representation of the activity
                ctx.json(mapper.writeValueAsString(activity))
            } else {
                // If the activity is not found, return a 404 Not Found status with an error message
                ctx.status(404).result("Activity not found")
            }
        } catch (e: Exception) {
            // If an unexpected error occurs, return a 500 Internal Server Error
            ctx.status(500).result("Internal server error")
        }
    }


}