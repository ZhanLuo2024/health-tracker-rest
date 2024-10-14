package ie.setu.controllers

import ie.setu.domain.User
import ie.setu.domain.repository.UserDAO
import io.javalin.http.Context

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue


object HealthTrackerController {

    private val userDao = UserDAO()

    fun getAllUsers(ctx: Context) {
        ctx.json(userDao.getAll())
    }

    fun getUserByUserId(ctx: Context) {
        val user = userDao.getById(ctx.pathParam("user-id").toInt())
        if (user != null) {
            ctx.json(user)
        }
    }

    fun addUser(ctx: Context) {
        val mapper = jacksonObjectMapper()
        val user = mapper.readValue<User>(ctx.body())
        userDao.save(user)
        ctx.json(user)
    }

    fun getUserByEmail(ctx: Context) {
        val user = userDao.getByEmail(ctx.pathParam("user-email").toString())
        if (user != null) {
            ctx.json(user)
        }
    }


}