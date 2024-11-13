package ie.setu.domain.repository

import ie.setu.domain.User
import ie.setu.domain.UserRegistration
import ie.setu.domain.db.Users
import ie.setu.utils.mapToUser
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.jetbrains.exposed.sql.insert


class UserRegisterDAO {
    // Register a new user
    fun registerUser(user: UserRegistration): User = transaction {
        val userId = Users.insert {
            it[name] = user.name
            it[email] = user.email
            it[password] = user.password
            it[isLoggedIn] = false  // Set default logged-in status to false
        } get Users.id

        User(
            id = userId,
            name = user.name,
            email = user.email,
            password = user.password,
            isLoggedIn = false
        )
    }

    // Find a user by email and password (for login)
    fun findByEmailAndPassword(email: String, password: String): User? = transaction {
        Users.selectAll().where { (Users.email eq email) and (Users.password eq password) }
            .mapNotNull { mapToUser(it) }
            .singleOrNull()
    }

    // Update user login status
    fun updateLoginStatus(userId: Int, isLoggedIn: Boolean): Boolean = transaction {
        Users.update({ Users.id eq userId }) {
            it[Users.isLoggedIn] = isLoggedIn
        } > 0
    }

}