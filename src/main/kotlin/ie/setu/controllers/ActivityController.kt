package ie.setu.controllers

import ie.setu.domain.Activity
import ie.setu.domain.repository.ActivityDAO
import io.javalin.http.Context
import ie.setu.domain.repository.UserDAO
import ie.setu.utils.jsonObjectMapper
import ie.setu.utils.jsonToObject

class ActivityController {

    private val userDao = UserDAO()
    private val activityDAO = ActivityDAO()

    //--------------------------------------------------------------
    // ActivityDAO specifics
    //-------------------------------------------------------------

    fun getAllActivities(ctx: Context) {
        //mapper handles the deserialization of Joda date into a String.
        ctx.json(jsonObjectMapper().writeValueAsString( activityDAO.getAll() ))
    }

    fun getActivitiesByUserId(ctx: Context) {
        if (userDao.findById(ctx.pathParam("user-id").toInt()) != null) {
            val activities = activityDAO.findByUserId(ctx.pathParam("user-id").toInt())
            if (activities.isNotEmpty()) {
                //mapper handles the deserialization of Joda date into a String.
                ctx.json(jsonObjectMapper().writeValueAsString(activities))
            }
        }
    }

    fun addActivity(ctx: Context) {
        //mapper handles the serialisation of Joda date into a String.
        val activity : Activity = jsonToObject(ctx.body())
        activityDAO.save(activity)
        ctx.json(activity)
    }

    fun deleteActivityByUserId(ctx: Context){
        activityDAO.deleteByUserId(ctx.pathParam("user-id").toInt())
    }

    fun deleteActivityByActivityId(ctx: Context){
        activityDAO.deleteByActivityId(ctx.pathParam("activity-id").toInt())
    }

    fun updateActivity(ctx: Context){
        val activity : Activity = jsonToObject(ctx.body())
        if (activityDAO.updateByActivityId(
                activityId = ctx.pathParam("activity-id").toInt(),
                activityToUpdate = activity) != 0)
            ctx.status(204)
        else
            ctx.status(404)
    }
}