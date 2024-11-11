package ie.setu.controllers

import ie.setu.domain.UserRegistration
import ie.setu.domain.repository.UserDAO
import ie.setu.domain.repository.UserRegisterDAO
import io.javalin.http.Context
import org.joda.time.DateTime


class UserRegisterController {

    private val userRegisterDAO: UserRegisterDAO = UserRegisterDAO()

    // Register a new user
    fun registerUser(ctx: Context) {
        val userRegisterDAO = UserRegisterDAO()
        val name = ctx.formParam("name") ?: run {
            ctx.status(400).result("Name is required")
            return
        }
        val email = ctx.formParam("email") ?: run {
            ctx.status(400).result("Email is required")
            return
        }
        val password = ctx.formParam("password") ?: run {
            ctx.status(400).result("Password is required")
            return
        }

        val newUser = userRegisterDAO.registerUser(UserRegistration(name, email, password))
        ctx.status(201).json(newUser)
    }

    // User login
    fun loginUser(ctx: Context) {
        val userRegisterDAO = UserRegisterDAO()
        val email = ctx.formParam("email") ?: run {
            ctx.status(400).result("Email is required")
            return
        }
        val password = ctx.formParam("password") ?: run {
            ctx.status(400).result("Password is required")
            return
        }

        val user = userRegisterDAO.findByEmailAndPassword(email, password)
        if (user != null) {
            userRegisterDAO.updateLoginStatus(user.id, true)
            ctx.status(200).result("Login successful")
        } else {
            ctx.status(401).result("Invalid email or password")
        }
    }

    // User logout
    fun logoutUser(ctx: Context) {
        val userRegisterDAO = UserRegisterDAO()
        val userId = ctx.pathParam("user-id").toIntOrNull() ?: run {
            ctx.status(400).result("Invalid user ID format")
            return
        }

        if (userRegisterDAO.updateLoginStatus(userId, false)) {
            ctx.status(200).result("Logout successful")
        } else {
            ctx.status(404).result("User not found")
        }
    }



}