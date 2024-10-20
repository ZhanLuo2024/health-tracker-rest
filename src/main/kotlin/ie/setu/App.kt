package ie.setu

import ie.setu.config.DbConfig
import ie.setu.config.JavalinConfig

fun main(args: Array<String>) {

    DbConfig().getDbConnection()
    JavalinConfig().startJavalinService()
}