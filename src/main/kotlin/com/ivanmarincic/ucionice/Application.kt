package com.ivanmarincic.ucionice

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.ivanmarincic.ucionice.controller.AuthenticationController
import com.ivanmarincic.ucionice.model.*
import com.ivanmarincic.ucionice.service.UsersService
import com.ivanmarincic.ucionice.util.exceptions.AuthenticationFailedException
import com.ivanmarincic.ucionice.util.exceptions.AuthorizationFailedException
import com.ivanmarincic.ucionice.util.exceptions.UserExistsException
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource
import com.j256.ormlite.table.TableUtils
import io.javalin.Javalin
import io.javalin.core.security.Role
import io.javalin.http.Context
import io.javalin.http.Handler
import io.javalin.plugin.json.JavalinJackson
import org.eclipse.jetty.http.HttpHeader
import org.eclipse.jetty.http.HttpStatus
import java.io.File

private val usersService by lazy {
    UsersService()
}
private val ignoredPaths = setOf("/auth/login", "/auth/register", "/swagger", "/swagger-docs")

class Application {
    companion object {
        lateinit var connectionSource: JdbcPooledConnectionSource
    }
}

fun authorizeRequest(handler: Handler, ctx: Context, permittedRoles: Set<Role>) {
    if (ignoredPaths.contains(ctx.endpointHandlerPath())) {
        handler.handle(ctx)
    } else {
        val user = usersService
            .authorize(
                ctx.header(HttpHeader.AUTHORIZATION.toString()) ?: throw AuthorizationFailedException()
            )
        handler.handle(ctx)
    }
}

fun setupDatabase() {
    if (!File("database.sqlite").exists()) {
        File("database.sqlite").createNewFile()
    }
    Application.connectionSource = JdbcPooledConnectionSource("jdbc:sqlite:database.sqlite")
    Application.connectionSource.let {
        TableUtils.createTableIfNotExists(it, User::class.java)
        TableUtils.createTableIfNotExists(it, Group::class.java)
        TableUtils.createTableIfNotExists(it, GroupSettings::class.java)
        TableUtils.createTableIfNotExists(it, GroupInvitation::class.java)
        TableUtils.createTableIfNotExists(it, GroupClassroom::class.java)
        TableUtils.createTableIfNotExists(it, Classroom::class.java)
        TableUtils.createTableIfNotExists(it, ClassroomFeature::class.java)
        TableUtils.createTableIfNotExists(it, Appointment::class.java)
    }
}

fun main(args: Array<String>) {
    setupDatabase()
    JavalinJackson.configure(ObjectMapper().registerModule(KotlinModule()))
    Javalin
        .create {
            it.accessManager(::authorizeRequest)
            it.contextPath = "/ucionice"
        }
        .routes(
            AuthenticationController()
        )
        .exception(AuthenticationFailedException::class.java) { _, ctx ->
            ctx.status(HttpStatus.BAD_REQUEST_400).result("Authentication failed")
        }
        .exception(AuthorizationFailedException::class.java) { _, ctx ->
            ctx.status(HttpStatus.UNAUTHORIZED_401).result("Unauthorized")
        }
        .exception(UserExistsException::class.java) { e, ctx ->
            ctx.status(HttpStatus.BAD_REQUEST_400).result("User with email ${e.email} already exists")
        }
        .start(5005)
}

enum class UserRole(type: Int) : Role {
    ANYONE(1), OWNER(2), MANAGER(3), USER(3)
}

enum class ClassroomFeatureType(type: Int) : Role {
    NONE(1), CAPACITY(2), PROJECTOR(3), COMPUTER(4)
}
