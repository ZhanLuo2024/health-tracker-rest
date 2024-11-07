package ie.setu.config

import ie.setu.routes.registerWaterIntakeRoutes
import ie.setu.routes.registerUserRoutes
import ie.setu.routes.registerActivityRoutes
import ie.setu.routes.registerCalorieLogRoutes
import io.javalin.Javalin
import io.javalin.json.JavalinJackson
import ie.setu.utils.jsonObjectMapper

class JavalinConfig {
    fun startJavalinService(): Javalin {
        val app = Javalin.create {
            //add this jsonMapper to serialise objects to json
            it.jsonMapper(JavalinJackson(jsonObjectMapper()))
        }
            .apply{
                exception(Exception::class.java) { e, ctx -> e.printStackTrace() }
                error(404) { ctx -> ctx.json("404 - Not Found") }
            }
            .start(getRemoteAssignedPort())

        registerRoutes(app)
        return app
    }

    private fun registerRoutes(app: Javalin) {
        // user interface
        registerUserRoutes(app)

        // activity interface
        registerActivityRoutes(app)

        //register water intake routes
        registerWaterIntakeRoutes(app)

        // register calorie log routes
        registerCalorieLogRoutes(app)

    }

    private fun getRemoteAssignedPort(): Int {
        val remotePort = System.getenv("PORT")
        return if (remotePort != null) {
            Integer.parseInt(remotePort)
        } else 7001
    }
}