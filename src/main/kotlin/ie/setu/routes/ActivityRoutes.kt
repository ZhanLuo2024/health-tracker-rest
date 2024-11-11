package ie.setu.routes

import ie.setu.controllers.ActivityController
import io.javalin.Javalin

fun registerActivityRoutes(app: Javalin) {
    val controller = ActivityController()

    app.get("/api/activities", controller::getAllActivities)
    app.get("/api/users/{user-id}/activities", controller::getActivitiesByUserId)

    app.post("/api/activities", controller::addActivity)

    app.patch("/api/activities/{activity-id}", controller::updateActivity)

    app.delete("/api/activities/{activity-id}", controller::deleteActivityByActivityId)
    app.delete("/api/users/{user-id}/activities", controller::deleteActivityByUserId)

}