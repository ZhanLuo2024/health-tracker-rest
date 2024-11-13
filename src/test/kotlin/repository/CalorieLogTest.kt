package repository

import ie.setu.domain.CalorieLog
import ie.setu.domain.db.CalorieLogs
import ie.setu.domain.db.Users
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.junit.jupiter.api.*
import ie.setu.domain.repository.CalorieLogsDAO
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows



class CalorieLogDAOTest {

    private val calorieLogDAO = CalorieLogsDAO()
    val fixedTime: DateTime = DateTime.now().withTimeAtStartOfDay()

    companion object {
        // Make a connection to a local, in memory H2 database.
        @BeforeAll
        @JvmStatic
        internal fun setupInMemoryDatabaseConnection() {
            Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver", user = "root", password = "")
        }
    }

    private fun populateCalorieLogTable(): CalorieLogsDAO {
        transaction {
            SchemaUtils.create(Users, CalorieLogs) // 确保 Users 和 CalorieLogs 表被创建
            // Insert a mock user for testing
            Users.insert {
                it[id] = 1
                it[name] = "Test User"
                it[email] = "testuser@example.com" // 添加 email 字段的值
                it[password] = "default_password" // Provide a default password value
            }

            // Insert calorie records for the user
            calorieLogDAO.add(CalorieLog(0, 500, fixedTime.minusDays(1), 1))
            calorieLogDAO.add(CalorieLog(0, 300, fixedTime, 1))
            calorieLogDAO.add(CalorieLog(0, 700, fixedTime.plusDays(1), 1))

        }
        return calorieLogDAO
    }


    @Nested
    inner class CreateCalorieLog {
        @Test
        fun `multiple calorie logs added to table can be retrieved successfully`() {
            transaction {
                // Arrange - create and populate table with three calorie records
                val calorieLogDAO = populateCalorieLogTable()

                // Act & Assert
                val logs = calorieLogDAO.findByUserId(1)
                assertEquals(3, logs.size)
            }
        }
    }

    @Nested
    inner class ReadCalorieLogs {

        @Test
        fun `test find calorie records by userId with date range`() {
            transaction {
                // Arrange - create and populate table with three calorie records
                val calorieLogDAO = populateCalorieLogTable()

                val now = DateTime.now()
                val earlier = now.minusDays(1)

                val logs = calorieLogDAO.findByUserId(1, start = fixedTime.minusDays(1), end = fixedTime)
                assertEquals(2, logs.size)
            }
        }


        @Test
        fun `test add calorie record with negative calories throws exception`() {
            transaction {
                // Arrange - create and populate table with a mock user
                SchemaUtils.create(Users)
                Users.insert {
                    it[id] = 1
                    it[name] = "Test User"
                    it[email] = "testuser@example.com"
                    it[password] = "default_password"
                }

                // Act & Assert
                val newCalorieLog = CalorieLog(userId = 1, calories = -100, recordedAt = DateTime.now())
                assertThrows(IllegalArgumentException::class.java) {
                    calorieLogDAO.add(newCalorieLog)
                }
            }
        }

    }

    @Nested
    inner class FindCalorieLogById {
        @Test
        fun `find calorie log by ID - success`() {
            transaction {
                // Arrange - create and populate table with calorie log records
                val calorieLogDAO = populateCalorieLogTable()

                // Act & Assert - verify the calorie log is found successfully
                Assertions.assertNotNull(calorieLogDAO.findByCalorieLogId(1))
            }
        }

        @Test
        fun `find calorie log by ID - not found`() {
            transaction {
                // Arrange - create and populate table with calorie log records
                val calorieLogDAO = populateCalorieLogTable()

                // Act & Assert - verify the calorie log is not found
                Assertions.assertNull(calorieLogDAO.findByCalorieLogId(999))
            }
        }
    }

    @Nested
    inner class DeleteCalorieLogs {
        @Test
        fun `delete all calorie logs by user ID - success`() {
            transaction {
                // Arrange - create and populate table with calorie log records
                val calorieLogDAO = populateCalorieLogTable()

                // Act & Assert - verify all logs for user are deleted successfully
                Assertions.assertTrue(calorieLogDAO.deleteByUserId(1))
                Assertions.assertEquals(0, calorieLogDAO.findByUserId(1).size)
            }
        }

        @Test
        fun `delete calorie log by ID - success`() {
            transaction {
                // Arrange - create and populate table with calorie log records
                val calorieLogDAO = populateCalorieLogTable()

                // Act & Assert - verify the specific calorie log is deleted successfully
                Assertions.assertTrue(calorieLogDAO.deleteByCalorieLogId(1))
                Assertions.assertNull(calorieLogDAO.findByCalorieLogId(1))
            }
        }

        @Test
        fun `delete calorie log by ID - not found`() {
            transaction {
                // Arrange - create and populate table with calorie log records
                val calorieLogDAO = populateCalorieLogTable()

                // Act & Assert - verify no log is found for deletion
                Assertions.assertFalse(calorieLogDAO.deleteByCalorieLogId(999))
            }
        }

    }









}