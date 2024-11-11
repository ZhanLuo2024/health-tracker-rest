package repository

import ie.setu.domain.User
import ie.setu.domain.WaterIntake
import ie.setu.domain.db.Users
import ie.setu.domain.repository.WaterIntakeDAO
import ie.setu.domain.db.WaterIntakes
import ie.setu.domain.repository.UserDAO
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.junit.jupiter.api.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
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
        SchemaUtils.create(WaterIntakes)
        val waterIntakeDAO = WaterIntakeDAO()

        // create a user
        Users.insert {
            it[id] = 1
            it[name] = "Test User"
            it[email] = "testuser@example.com" // 添加 email 字段的值
            it[password] = "default_password" // Provide a default password value
        }

        // Insert test water intake records
        waterIntakeDAO.add(WaterIntake(0, 2.0, DateTime.now().minusDays(1), 1))
        waterIntakeDAO.add(WaterIntake(0, 1.5, DateTime.now(), 1))
        waterIntakeDAO.add(WaterIntake(0, 3.0, DateTime.now().plusDays(1), 1))

        return waterIntakeDAO
    }

    @Nested
    inner class FindWaterIntakeById {
        @Test
        fun `find water intake by ID - success`() {
            transaction {
                // Arrange - create and populate table with water intake records
                val waterIntakeDAO = populateWaterIntakeTable()

                // Act & Assert - verify the water intake is found successfully
                val waterIntake = waterIntakeDAO.findByWaterIntakeId(1)
                Assertions.assertNotNull(waterIntake)
                Assertions.assertEquals(1, waterIntake?.id)
            }
        }

        @Test
        fun `find water intake by ID - not found`() {
            transaction {
                // Arrange - create and populate table with water intake records
                val waterIntakeDAO = populateWaterIntakeTable()

                // Act & Assert - verify the water intake is not found
                val waterIntake = waterIntakeDAO.findByWaterIntakeId(999)
                Assertions.assertNull(waterIntake)
            }
        }
    }

    @Nested
    inner class DeleteWaterIntakes {
        @Test
        fun `delete all water intakes by user ID - success`() {
            transaction {
                // Arrange - create and populate table with water intake records
                val waterIntakeDAO = populateWaterIntakeTable()

                // Act & Assert - verify all records for user are deleted successfully
                Assertions.assertTrue(waterIntakeDAO.deleteByUserId(1))
                Assertions.assertEquals(0, waterIntakeDAO.findByUserId(1).size)
            }
        }

        @Test
        fun `delete water intake by ID - success`() {
            transaction {
                // Arrange - create and populate table with water intake records
                val waterIntakeDAO = populateWaterIntakeTable()

                // Act & Assert - verify the specific water intake is deleted successfully
                Assertions.assertTrue(waterIntakeDAO.deleteByWaterIntakeId(1))
                Assertions.assertNull(waterIntakeDAO.findByWaterIntakeId(1))
            }
        }

        @Test
        fun `delete water intake by ID - not found`() {
            transaction {
                // Arrange - create and populate table with water intake records
                val waterIntakeDAO = populateWaterIntakeTable()

                // Act & Assert - verify no record is found for deletion
                Assertions.assertFalse(waterIntakeDAO.deleteByWaterIntakeId(999))
            }
        }
    }

    @Nested
    inner class FindWaterIntakesByUserId {
        @Test
        fun `find all water intakes by user ID - success`() {
            transaction {
                // Arrange - create and populate table with water intake records
                val waterIntakeDAO = populateWaterIntakeTable()

                // Act & Assert - verify all records for user are retrieved successfully
                val waterIntakes = waterIntakeDAO.findByUserId(1)
                Assertions.assertEquals(3, waterIntakes.size)
            }
        }

        @Test
        fun `find all water intakes by user ID - not found`() {
            transaction {
                // Arrange - create and populate table with water intake records
                val waterIntakeDAO = populateWaterIntakeTable()

                // Act & Assert - verify no records are found for a non-existent user
                Assertions.assertEquals(0, waterIntakeDAO.findByUserId(999).size)
            }
        }
    }

    @Nested
    inner class FindWaterIntakesByDateRange {
        @Test
        fun `find water intakes by date range - success`() {
            transaction {
                // Arrange - create and populate table with water intake records
                val waterIntakeDAO = populateWaterIntakeTable()
                val startDate = DateTime.now().minusDays(2)
                val endDate = DateTime.now().plusDays(2)

                // Act & Assert - verify records are retrieved within the date range
                val waterIntakes = waterIntakeDAO.findByUserIdAndDateRange(1, startDate, endDate)
                Assertions.assertEquals(3, waterIntakes.size)
            }
        }

        @Test
        fun `find water intakes by date range - no records`() {
            transaction {
                // Arrange - create and populate table with water intake records
                val waterIntakeDAO = populateWaterIntakeTable()
                val startDate = DateTime.now().plusDays(2)
                val endDate = DateTime.now().plusDays(3)

                // Act & Assert - verify no records are found outside the date range
                val waterIntakes = waterIntakeDAO.findByUserIdAndDateRange(1, startDate, endDate)
                Assertions.assertEquals(0, waterIntakes.size)
            }
        }
    }




}
