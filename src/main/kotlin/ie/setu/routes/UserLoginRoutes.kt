package ie.setu.routes

import io.javalin.Javalin
import ie.setu.controllers.UserRegisterController

fun registerUserRegisterRoute(app: Javalin) {
    val controller = UserRegisterController()

    app.post("/api/users/register", controller::registerUser)
    app.post("/api/users/login", controller::loginUser)

}