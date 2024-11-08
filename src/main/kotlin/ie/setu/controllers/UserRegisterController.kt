package ie.setu.controllers

import ie.setu.domain.UserRegistration
import ie.setu.domain.repository.UserRegisterDAO
import io.javalin.http.Context
import org.joda.time.DateTime


class UserRegisterController {

    private val userRegisterDAO: UserRegisterDAO = UserRegisterDAO()

    // register new user
    fun registerUser(ctx: Context) {
        try {
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

            val newUser = UserRegistration(name = name, email = email, password = password, createdAt = DateTime.now())

            userRegisterDAO.registerNewUser(newUser)
            ctx.status(201).result("User registered successfully")
        } catch (e: Exception) {
            ctx.status(500).result("Internal server error")
        }
    }

    fun loginUser(ctx: Context) {
        try {
            val email = ctx.formParam("email") ?: run {
                ctx.status(400).result("Email is required")
                return
            }
            val password = ctx.formParam("password") ?: run {
                ctx.status(400).result("Password is required")
                return
            }

            val isLogin = userRegisterDAO.findByEmailAndPassword(email, password)

            if (!isLogin) {
                ctx.status(200).result("Login successful")
            } else {
                ctx.status(401).result("Invalid email or password")
            }
        } catch (e: Exception) {
            ctx.status(500).result("Internal server error")
        }
    }



}