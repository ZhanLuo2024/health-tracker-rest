package controllerstest


import ie.setu.controllers.WaterIntakeController
import ie.setu.domain.repository.WaterIntakeDAO
import io.javalin.Javalin
import io.javalin.http.HttpStatus
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assertions.*

class WaterIntakeControllerTest {
    private lateinit var app: Javalin
    private lateinit var waterIntakeDAO: WaterIntakeDAO
    private lateinit var waterIntakeController: WaterIntakeController


//    @BeforeEach
//    fun setUp() {
//        // Initialize DAO and app with the necessary routes
//        waterIntakeDAO = WaterIntakeDAO() // Use real DAO implementation
//        waterIntakeController = WaterIntakeController(waterIntakeDAO)
//        app = Javalin.create().apply {
//            WaterIntakeRoutes.registerRoutes(this, waterIntakeController)
//        }
//    }




}