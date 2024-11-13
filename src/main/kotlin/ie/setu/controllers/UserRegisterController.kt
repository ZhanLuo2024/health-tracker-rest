package ie.setu.controllers

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import ie.setu.domain.User
import ie.setu.domain.UserRegistration
import ie.setu.domain.db.Users
import ie.setu.domain.repository.UserDAO
import ie.setu.domain.repository.UserRegisterDAO
import io.javalin.http.Context
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime


class UserRegisterController {

    private val userRegisterDAO: UserRegisterDAO = UserRegisterDAO()

    // Register a new user
    fun registerUser(ctx: Context) {
        val mapper = jacksonObjectMapper()
        val userRegistration = mapper.readValue<UserRegistration>(ctx.body())

        if (userRegistration.name.isBlank()) {
            ctx.status(400).json(mapOf("error" to "Name is required"))
            return
        }

        val existingUser = transaction {
            Users.selectAll().where { Users.name eq userRegistration.name }.firstOrNull()
        }

        if (existingUser != null) {
            ctx.status(400).json(mapOf("error" to "A user with this name already exists"))
            return
        }

        if (userRegistration.email.isBlank()) {
            ctx.status(400).json(mapOf("error" to "Email is required"))
            return
        }

        if (userRegistration.password.isBlank()) {
            ctx.status(400).json(mapOf("error" to "Password is required"))
            return
        }

        if (userRegistration.password.length < 6) {
            ctx.status(400).json(mapOf("error" to "Password length must be more than 6"))
            return
        }

        val userRegisterDAO = UserRegisterDAO()

        val newUser = userRegisterDAO.registerUser(userRegistration)
        ctx.status(201).json(newUser)
    }


    // User login
    fun loginUser(ctx: Context) {
        val objectMapper = jacksonObjectMapper()
        val userPayload: Map<String, Any> = objectMapper.readValue(ctx.body())

        val email = userPayload["email"] as? String ?: run {
            ctx.status(400).result("Email is required")
            return
        }

        val password = userPayload["password"] as? String ?: run {
            ctx.status(400).result("Password is required")
            return
        }


        val userRegisterDAO = UserRegisterDAO()
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
        val objectMapper = jacksonObjectMapper()
        val userPayload: Map<String, Any> = objectMapper.readValue(ctx.body())

        val userId = userPayload["id"] as? Int ?: run {
            ctx.status(400).result("userId is required")
            return
        }

        val userRegisterDAO = UserRegisterDAO()
        if (userRegisterDAO.updateLoginStatus(userId, false)) {
            ctx.status(200).result("Logout successful")
        } else {
            ctx.status(404).result("User not found")
        }
    }



}