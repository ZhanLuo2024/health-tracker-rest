package ie.setu.domain.repository

import ie.setu.domain.User
import ie.setu.domain.UserRegistration
import ie.setu.domain.db.Users
import ie.setu.utils.mapToUser
// sql
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class UserDAO {

    fun getAll(): ArrayList<User> {
        val userList: ArrayList<User> = arrayListOf()
        transaction {
            Users.selectAll().map {
                userList.add(mapToUser(it)) }
        }
        return userList
    }

    fun findById(id: Int): User?{
        return transaction {
            Users.selectAll().where { Users.id eq id }
                .map{mapToUser(it)}
                .firstOrNull()
        }
    }

    fun findByEmail(email: String) :User?{
        return transaction {
            Users.selectAll().where { Users.email eq email}
                .map{mapToUser(it)}
                .firstOrNull()
        }
    }

    fun delete(id: Int):Int {
        return transaction {
            Users.deleteWhere { Users.id eq id }
        }
    }

    // Save a new user to the database (for UserRegistration)
    fun save(user: UserRegistration): User = transaction {
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
            isLoggedIn = false
        )
    }

    // Overloaded save method to save a User object directly
    fun save(user: User): User = transaction {
        val userId = Users.insert {
            it[name] = user.name
            it[email] = user.email
            it[password] = "" // Password not provided in User, add logic if needed
            it[isLoggedIn] = user.isLoggedIn
        } get Users.id

        user.copy(id = userId)
    }


    fun update(id: Int, user: User): Int{
        return transaction {
            Users.update ({
                Users.id eq id}) {
                it[name] = user.name
                it[email] = user.email
            }
        }
    }
}