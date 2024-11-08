package ie.setu.domain

import org.joda.time.DateTime

data class User(var id: Int, var name: String, var email: String)

// This class is used specifically for user registration.
data class UserRegistration(
    var name: String,
    var email: String,
    var password: String,
    var createdAt: DateTime,
)