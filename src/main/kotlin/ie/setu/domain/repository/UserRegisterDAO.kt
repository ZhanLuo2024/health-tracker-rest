package ie.setu.domain.repository

import ie.setu.domain.UserRegistration
import ie.setu.domain.db.Users
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime

class UserRegisterDAO {
    // Login
    fun findByEmailAndPassword(email: String, password: String): Boolean = transaction {
        Users.selectAll().where { (Users.email eq email) and (Users.password eq password) }
            .singleOrNull() != null
    }

    // Register
    fun registerNewUser(user: UserRegistration) = transaction {
        Users.insert {
            it[name] = user.name
            it[email] = user.email
            it[password] = user.password
            it[createdAt] = DateTime.now()
        }
    }

}