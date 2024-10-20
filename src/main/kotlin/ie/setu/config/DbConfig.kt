package ie.setu.config

import org.jetbrains.exposed.sql.Database
import org.postgresql.util.PSQLException
import io.github.oshai.kotlinlogging.KotlinLogging

class DbConfig {

    private val logger = KotlinLogging.logger {}
    private lateinit var dbConfig: Database

    fun getDbConnection(): Database {

        val PGHOST = "dpg-cs770sbtq21c73coql60-a.frankfurt-postgres.render.com"
        val PGPORT = "5432"
        val PGUSER = "health_tracker_app_j2r9_user"
        val PGPASSWORD = "59zP4Xn3bV7QrAW9vIxMqFWKkGDMJ0uV"
        val PGDATABASE = "health_tracker_app_j2r9"

        //url format should be jdbc:postgresql://host:port/database
        val dbUrl = "jdbc:postgresql://$PGHOST:$PGPORT/$PGDATABASE"

        try {
            logger.info { "Starting DB Connection...$dbUrl" }
            dbConfig = Database.connect(
                url = dbUrl, driver = "org.postgresql.Driver",
                user = PGUSER, password = PGPASSWORD
            )
            logger.info { "DB Connected Successfully..." + dbConfig.url }
        } catch (e: PSQLException) {
            logger.info { "Error in DB Connection...${e.printStackTrace()}" }
        }
//        try {
//
//            dbConfig = Database.connect(
//                url = dbUrl, driver = "org.postgresql.Driver",
//                user = PGUSER, password = PGPASSWORD
//            )
//
//        } catch (e: PSQLException) {
//
//        }
        return dbConfig

    }
}