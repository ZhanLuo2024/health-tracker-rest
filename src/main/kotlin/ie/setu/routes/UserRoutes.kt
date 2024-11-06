package ie.setu.routes

import ie.setu.controllers.UsersController
import io.javalin.Javalin

    fun registerUserRoutes(app: Javalin) {
        val controller = UsersController()

        app.get("/api/users", controller::getAllUsers)
        app.get("/api/users/{user-id}", controller::getUserByUserId)
        app.post("/api/users", controller::addUser)
        app.delete("/api/users/{user-id}", controller::deleteUser)
        app.patch("/api/users/{user-id}", controller::updateUser)
        app.get("/api/users/email/{email}", controller::getUserByEmail)
    }
