package repository

import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import ie.setu.domain.WaterIntake
import ie.setu.domain.db.Users
import ie.setu.domain.repository.WaterIntakeDAO
import ie.setu.domain.db.WaterIntakes
import org.jetbrains.exposed.sql.*
import org.junit.jupiter.api.BeforeAll


class WaterIntakeDAOTest {

    private var waterIntakeDAO = WaterIntakeDAO()

    companion object {
        // Make a connection to a local, in memory H2 database.
        @BeforeAll
        @JvmStatic
        internal fun setupInMemoryDatabaseConnection() {
            Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;", driver = "org.h2.Driver", user = "root", password = "")
        }
    }

    @BeforeEach
    fun setup() {
        transaction {
            // Create the Users and WaterIntakes tables if they don't exist
            SchemaUtils.create(Users, WaterIntakes)

            // Insert a test user with userId = 1, only if it doesn't already exist
            if (Users.selectAll().where { Users.id eq 1 }.empty()) {
                Users.insert {
                    it[id] = 1
                    it[name] = "Test User"
                    it[email] = "testuser@example.com"
                }
            }
        }
        waterIntakeDAO = WaterIntakeDAO()
    }

    @Test
    fun `test add water intake`() {
        transaction {
            val waterIntake = WaterIntake(
                id = 0,
                amount = 2.5,
                recordedAt = DateTime.now(),
                userId = 1
            )
            waterIntakeDAO.add(waterIntake)
            val results = waterIntakeDAO.findByUserId(1)
            assertEquals(1, results.size)
            assertEquals(2.5, results[0].amount)
            assertEquals(1, results[0].userId)
        }
    }

    @Test
    fun `test find water intake by user id`() {
        transaction {
            val waterIntake1 = WaterIntake(
                id = 0,
                amount = 1.5,
                recordedAt = DateTime.now(),
                userId = 1
            )
            val waterIntake2 = WaterIntake(
                id = 0,
                amount = 2.0,
                recordedAt = DateTime.now(),
                userId = 1
            )
            waterIntakeDAO.add(waterIntake1)
            waterIntakeDAO.add(waterIntake2)
            val results = waterIntakeDAO.findByUserId(1)
            assertEquals(2, results.size)
            assertEquals(1.5, results[0].amount)
            assertEquals(2.0, results[1].amount)
        }
    }

    @Test
    fun `test find water intake by user id and date range`() {
        transaction {
            val now = DateTime.now()
            val waterIntake1 = WaterIntake(
                id = 0,
                amount = 1.5,
                recordedAt = now.minusDays(1),
                userId = 1
            )
            val waterIntake2 = WaterIntake(
                id = 0,
                amount = 2.0,
                recordedAt = now,
                userId = 1
            )
            waterIntakeDAO.add(waterIntake1)
            waterIntakeDAO.add(waterIntake2)

            val results = waterIntakeDAO.findByUserIdAndDateRange(1, now.minusDays(1), now.plusDays(1))
            assertEquals(2, results.size)
            assertEquals(1.5, results[0].amount)
            assertEquals(2.0, results[1].amount)
        }
    }



}