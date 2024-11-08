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
    val fixedTime = DateTime.now().withTimeAtStartOfDay()

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
                }

                // Act & Assert
                val newCalorieLog = CalorieLog(userId = 1, calories = -100, recordedAt = DateTime.now())
                assertThrows(IllegalArgumentException::class.java) {
                    calorieLogDAO.add(newCalorieLog)
                }
            }
        }

    }









}