package ie.setu.domain

data class User(var id: Int, var name: String, var email: String)

// This class is used specifically for user registration.
data class UserRegistration(
    val name: String,
    val email: String,
    val password: String
)