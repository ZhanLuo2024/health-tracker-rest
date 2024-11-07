package repository

import ie.setu.domain.User
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.junit.jupiter.api.Test
import ie.setu.domain.WaterIntake
import ie.setu.domain.db.Users
import ie.setu.domain.repository.WaterIntakeDAO
import ie.setu.domain.db.WaterIntakes
import ie.setu.domain.repository.UserDAO
import org.jetbrains.exposed.sql.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Assertions


class WaterIntakeDAOTest {

    private var waterIntakeDAO = WaterIntakeDAO()

    companion object {

        // Make a connection to a local, in memory H2 database.
        @BeforeAll
        @JvmStatic
        internal fun setupInMemoryDatabaseConnection() {
            Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver", user = "root", password = "")
        }
    }

    private fun populateWaterIntakeTable(): WaterIntakeDAO {
        SchemaUtils.create(Users, WaterIntakes)
        val userDAO = UserDAO()
        val waterIntakeDAO = WaterIntakeDAO()

        // Insert a test user
        userDAO.save(User(1, "Test User", "testuser@example.com"))

        // Insert water intake records for the user
        waterIntakeDAO.add(WaterIntake(0, 2.0, DateTime.now().minusDays(1), 1))
        waterIntakeDAO.add(WaterIntake(0, 1.5, DateTime.now(), 1))
        waterIntakeDAO.add(WaterIntake(0, 3.0, DateTime.now().plusDays(1), 1))

        return waterIntakeDAO
    }

    @Nested
    inner class CreateWaterIntake {
        @Test
        fun `multiple water intakes added to table can be retrieved successfully`() {
            transaction {

                // Arrange - create and populate table with three water intake records
                val waterIntakeDAO = populateWaterIntakeTable()

                // Act & Assert
                Assertions.assertEquals(3, waterIntakeDAO.findByUserId(1).size)
            }
        }
    }

    @Nested
    inner class ReadWaterIntakes {
        @Test
        fun `getting all water intakes from a populated table returns all rows`() {
            transaction {

                // Arrange - create and populate table with three water intake records
                val waterIntakeDAO = populateWaterIntakeTable()

                // Act & Assert
                Assertions.assertEquals(3, waterIntakeDAO.findByUserId(1).size)
            }
        }

        @Test
        fun `get water intake by user id that doesn't exist, results in no records returned`() {
            transaction {

                // Arrange - create and populate table with three water intake records
                val waterIntakeDAO = populateWaterIntakeTable()

                // Act & Assert
                Assertions.assertEquals(0, waterIntakeDAO.findByUserId(999).size)
            }
        }
    }

    @Nested
    inner class GetWaters {
     @Test
     fun `get water intake by date range returns correct records`() {
         transaction {

            // Arrange - create and populate table with three water intake records
            val waterIntakeDAO = populateWaterIntakeTable()
            val startDate = DateTime.now().minusDays(2)
            val endDate = DateTime.now().plusDays(2)

            // Act & Assert
            Assertions.assertEquals(3, waterIntakeDAO.findByUserIdAndDateRange(1, startDate, endDate).size)
         }
     }
}








}