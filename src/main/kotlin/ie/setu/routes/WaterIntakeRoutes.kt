package ie.setu.routes

import ie.setu.controllers.WaterIntakeController
import io.javalin.Javalin

fun registerWaterIntakeRoutes(app: Javalin) {
    val controller = WaterIntakeController()

    app.get("/api/waterIntakes/user/{user-id}", controller::getWaterIntakeByUserId)
    app.get("/api/waterintakes/{waterintake-id}", controller::getWaterIntakeById)
    app.get("/api/waterintakes/daily/user/{user-id}", controller::getDailyWaterIntakeSummary)

    app.post("/api/waterintakes", controller::addWaterIntake)

    app.delete("/api/waterintakes/{waterintakes-id}", controller::deleteWaterIntakesByWaterIntakeId)
    app.delete("/api/users/{user-id}/waterintakes", controller::deleteWaterIntakeByUserId)


}