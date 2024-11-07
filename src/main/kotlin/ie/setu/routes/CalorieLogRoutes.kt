package ie.setu.routes

import ie.setu.controllers.CalorieLogController
import io.javalin.Javalin

fun registerCalorieLogRoutes(app: Javalin) {
    val controller = CalorieLogController()

    app.get("/api/calories/user/{user-id}", controller::getCalorieLogByUserId)
    app.post("/api/calories", controller::addCalorieLog)

}