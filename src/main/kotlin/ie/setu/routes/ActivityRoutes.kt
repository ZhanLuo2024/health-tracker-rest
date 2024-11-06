package ie.setu.routes

import ie.setu.controllers.ActivityController
import io.javalin.Javalin

fun registerActivityRoutes(app: Javalin) {
    val controller = ActivityController()

    app.get("/api/activities", controller::getAllActivities)
    app.post("/api/activities", controller::addActivity)
    app.get("/api/users/{user-id}/activities", controller::getActivitiesByUserId)
    app.get("/api/users/activities/{activity-id}", controller::getActivityById)

}