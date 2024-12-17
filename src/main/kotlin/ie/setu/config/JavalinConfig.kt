package ie.setu.config

import ie.setu.routes.registerWaterIntakeRoutes
import ie.setu.routes.registerUserRoutes
import ie.setu.routes.registerActivityRoutes
import ie.setu.routes.registerCalorieLogRoutes
import ie.setu.routes.registerUserRegisterRoute
import io.javalin.Javalin
import io.javalin.json.JavalinJackson
import ie.setu.utils.jsonObjectMapper
import io.javalin.vue.VueComponent

class JavalinConfig {
    fun startJavalinService(): Javalin {
        val app = Javalin.create {
            //add this jsonMapper to serialise objects to json
            it.jsonMapper(JavalinJackson(jsonObjectMapper()))
            it.staticFiles.enableWebjars()
            it.vue.vueInstanceNameInJs = "app" // only required for Vue 3, is defined in layout.html
        }
            .apply{
                exception(Exception::class.java) { e, ctx -> e.printStackTrace() }
                error(404) { ctx -> ctx.json("404 - Not Found") }
            }
            .start(getRemoteAssignedPort())

        registerRoutes(app)
        registerHomePage(app)
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

        // register user register routes
        registerUserRegisterRoute(app)

    }

    private fun registerHomePage(app: Javalin) {
        // The @routeComponent that we added in layout.html earlier will be replaced
        // by the String inside the VueComponent. This means a call to / will load
        // the layout and display our <home-page> component.
        app.get("/", VueComponent("<home-page></home-page>"))
        app.get("/users", VueComponent("<user-overview></user-overview>"))
        app.get("/users/{user-id}", VueComponent("<user-profile></user-profile>"))

        //activity
        app.get("/activities", VueComponent("<user-activity-overview></user-activity-overview>"))

    }

    private fun getRemoteAssignedPort(): Int {
        val remotePort = System.getenv("PORT")
        return if (remotePort != null) {
            Integer.parseInt(remotePort)
        } else 7001
    }
}