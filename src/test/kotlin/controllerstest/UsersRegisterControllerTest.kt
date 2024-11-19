package controllerstest

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import ie.setu.config.DbConfig
import ie.setu.controllers.UserRegisterController
import ie.setu.domain.db.Users
import ie.setu.helpers.*
import kong.unirest.core.HttpResponse
import kong.unirest.core.JsonNode
import kong.unirest.core.Unirest
import org.junit.jupiter.api.*
import io.javalin.Javalin
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Nested

class UsersRegisterControllerTest {

    companion object {
        private val db = DbConfig().getDbConnection()
        private var app = ServerContainer.instance
        private val origin = "http://localhost:" + app.port()
        private val objectMapper = jacksonObjectMapper()
        private var serverPort = 7070

        private fun registerRoute(app: Javalin) {
            val controller = UserRegisterController()

            app.post("/api/users/register", controller::registerUser)
            app.post("/api/users/login", controller::loginUser)
            app.get("/api/users/logout/{user-id}", controller::logoutUser)

        }

        @BeforeAll
        @JvmStatic
        fun setup() {
            // Start Javalin server
            app = Javalin.create().start(serverPort)

            // Set up routes
            registerRoute(app)

            // Use the test environment database connection
            db

            // Create the USERS table before running tests
            transaction {
                SchemaUtils.create(Users)
            }
        }

        @AfterAll
        @JvmStatic
        fun tearDown() {
            // Stop Javalin server after all tests
            app.stop()
        }
    }

    @Nested
    inner class RegisterUserTests {
        @Test
        fun `register user - successfully`() {

            // Arrange - create a new user registration
            val userPayload = mapOf(
                "name" to "Test User",
                "email" to "testuser@example.com",
                "password" to "password123"
            )

            // Act - make POST request to register user
            val response = makePostRequest("/api/users/register" , userPayload)

            // Assert - verify response status and content
            Assertions.assertEquals(201, response.status)
            val responseBody = response.body.`object`
            Assertions.assertEquals("Test User", responseBody.getString("name"))
            Assertions.assertEquals("testuser@example.com", responseBody.getString("email"))

        }

        @Test
        fun `register user with short password - failure`() {
            // Arrange - create user registration payload with short password
            val userPayload = mapOf(
                "name" to "Test User",
                "email" to "testuser2@example.com",
                "password" to "short"
            )

            // Act - make POST request to register user
            val response = makePostRequest("/api/users/register" , userPayload)

            // Assert - verify response status and error message
            Assertions.assertEquals(400, response.status)
        }

        @Test
        fun `register duplicate user - failure`() {
            // Arrange - create user registration payload
            val userPayload = mapOf(
                "name" to "Test User",
                "email" to "duplicateuser@example.com",
                "password" to "password123"
            )

            // Act - make POST request to register user twice
            
            val response = makePostRequest("/api/users/register" , userPayload)

            // Assert - verify response status and error message
            Assertions.assertEquals(400, response.status)
        }
    }

    @Nested
    inner class UserLoginLogoutTests {
        @Test
        fun `login user successfully - integration test`() {

            // Act - login user
            val loginPayload = mapOf(
                "email" to "testuser@example.com",
                "password" to "password123"
            )
            val response = makePostRequest("/api/users/login", loginPayload)

            // Assert - verify response status
            Assertions.assertEquals(200, response.status)
        }

        @Test
        fun `logout user successfully - integration test`() {

            val userId = 127
            val response = Unirest.get("$origin/api/users/logout/$userId").asJson()

            // Assert - verify response status
            Assertions.assertEquals(200, response.status)
        }
    }

    // private func
    private fun makePostRequest(URLStr: String ,userPayload: Map<String, String>): HttpResponse<JsonNode> {
        return Unirest.post(origin + URLStr)
            .header("Content-Type", "application/json")
            .body(objectMapper.writeValueAsString(userPayload))
            .asJson()
    }

}