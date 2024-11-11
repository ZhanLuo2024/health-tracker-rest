package repository

import ie.setu.domain.UserRegistration
import ie.setu.domain.db.Users
import ie.setu.domain.repository.UserRegisterDAO
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions
import org.joda.time.DateTime
import org.junit.jupiter.api.Assertions.assertTrue



@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserRigsterDAOTest {
    private var userRegisterDAO = UserRegisterDAO()

    companion object {
        // Make a connection to a local, in memory H2 database.
        @BeforeAll
        @JvmStatic
        internal fun setupInMemoryDatabaseConnection() {
            Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver", user = "root", password = "")
        }
    }

    private fun populateUserTable(): UserRegisterDAO {
        SchemaUtils.create(Users)
        val userDAO = UserRegisterDAO()

        // register test users
        userDAO.registerUser(UserRegistration("Test User 1", "user1@example.com", "password1"))
        userDAO.registerUser(UserRegistration("Test User 2", "user2@example.com", "password2"))
        return userDAO
    }

    @Test
    fun `find user by email and password - success`() {
        transaction {
            // Arrange - create and populate table with user records
            val userDAO = populateUserTable()

            // Act & Assert - verify the user is found successfully
            val user = userDAO.findByEmailAndPassword("user1@example.com", "password1")
            Assertions.assertTrue(user != null, "User should exist with given email and password")
        }
    }

    @Test
    fun `find user by email and password - user does not exist`() {
        transaction {
            // Arrange - create and populate table with user records
            val userDAO = populateUserTable()

            // Act & Assert - verify the user is not found
            val user = userDAO.findByEmailAndPassword("user1@example.com", "password1")
            Assertions.assertTrue(user != null, "User should exist with given email and password")
        }
    }

    @Test
    fun `register user without password - failure`() {
        transaction {
            // Arrange - create a new user without a password
            val user = UserRegistration("Test User 3", "user3@example.com", "")

            // Act & Assert - verify saving the user fails due to missing or invalid password
            Assertions.assertThrows(IllegalArgumentException::class.java) {
                userRegisterDAO.registerUser(user)
            }
        }
    }

    @Test
    fun `save user with short password - failure`() {
        transaction {
            // Arrange - create a new user with a password shorter than 6 characters
            val user = UserRegistration("Test User 4", "user4@example.com", "short")

            // Act & Assert - verify saving the user fails due to short password
            Assertions.assertThrows(IllegalArgumentException::class.java) {
                userRegisterDAO.registerUser(user)
            }
        }
    }



}