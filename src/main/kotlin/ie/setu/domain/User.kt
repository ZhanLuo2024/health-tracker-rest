package ie.setu.domain

import org.joda.time.DateTime

data class User(
    val id: Int = 0,
    val name: String,
    val email: String,
    val password: String = "default password",
    val isLoggedIn: Boolean = false
)

// User Registration Class
data class UserRegistration(
    val name: String,
    val email: String,
    val password: String
)