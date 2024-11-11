package ie.setu.routes

import ie.setu.controllers.CalorieLogController
import io.javalin.Javalin

fun registerCalorieLogRoutes(app: Javalin) {
    val controller = CalorieLogController()

    app.get("/api/calories/user/{user-id}", controller::getCalorieLogByUserId)
    app.get("/api/calorielogs/{calorie-log-id}", controller::findByCalorieLogId)

    app.post("/api/calories", controller::addCalorieLog)

    app.delete("/api/users/{user-id}/calorielogs", controller::deleteByUserId)
    app.delete("/api/calorielogs/{calorie-log-id}", controller::deleteByCalorieLogId)

}