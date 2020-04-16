package com.ivanmarincic.ucionice

import com.auth0.jwt.exceptions.TokenExpiredException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.ivanmarincic.ucionice.controller.*
import com.ivanmarincic.ucionice.model.*
import com.ivanmarincic.ucionice.service.GroupsService
import com.ivanmarincic.ucionice.service.UsersService
import com.ivanmarincic.ucionice.util.authenticatedUser
import com.ivanmarincic.ucionice.util.exceptions.*
import com.ivanmarincic.ucionice.util.routes
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource
import com.j256.ormlite.table.TableUtils
import io.javalin.Javalin
import io.javalin.core.security.Role
import io.javalin.http.Context
import io.javalin.http.Handler
import io.javalin.plugin.json.JavalinJackson
import io.javalin.plugin.openapi.OpenApiOptions
import io.javalin.plugin.openapi.OpenApiPlugin
import io.javalin.plugin.openapi.ui.SwaggerOptions
import io.swagger.v3.oas.models.info.Info
import org.eclipse.jetty.http.HttpHeader
import org.eclipse.jetty.http.HttpStatus
import org.eclipse.jetty.server.session.DefaultSessionCache
import org.eclipse.jetty.server.session.FileSessionDataStore
import org.eclipse.jetty.server.session.SessionHandler
import java.io.File
import java.util.*


private val usersService by lazy {
    UsersService()
}
private val groupsService by lazy {
    GroupsService()
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
        var user = ctx.authenticatedUser()
        if (user == null) {
            user = usersService
                .authorize(
                    ctx.header(HttpHeader.AUTHORIZATION.toString()) ?: throw AuthorizationFailedException()
                )
            ctx.sessionAttribute("user", user)
        } else {
            val expiration = ctx.sessionAttribute<Date>("expiration")
            if (expiration == null || expiration < Date()) {
                throw TokenExpiredException("")
            }
        }
        if (permittedRoles.contains(UserRole.ANYONE)) {
            handler.handle(ctx)
        } else {
            val groupUser = groupsService.getByGroupAndUser(ctx.header("Group")!!.toInt(), user)
                ?: throw GroupAuthorizationFailedException()
            if (permittedRoles.contains(groupUser.role)) {
                ctx.attribute("group", groupUser)
                handler.handle(ctx)
            } else {
                throw RoleAuthorizationFailedException()
            }
        }
    }
}

fun fileSessionHandler() = SessionHandler().apply {
    sessionCache = DefaultSessionCache(this).apply {
        sessionDataStore = FileSessionDataStore().apply {
            val baseDir = File(System.getProperty("java.io.tmpdir"))
            this.storeDir = File(baseDir, "javalin-session-store").apply { mkdir() }
        }
    }
    httpOnly = true
}

fun setupDatabase() {
    if (!File("database.sqlite").exists()) {
        File("database.sqlite").createNewFile()
    }
    Application.connectionSource = JdbcPooledConnectionSource("jdbc:sqlite:database.sqlite")
    Application.connectionSource.let {
        TableUtils.createTableIfNotExists(it, User::class.java)
        TableUtils.createTableIfNotExists(it, Group::class.java)
        TableUtils.createTableIfNotExists(it, GroupUser::class.java)
        TableUtils.createTableIfNotExists(it, GroupInvitation::class.java)
        TableUtils.createTableIfNotExists(it, Classroom::class.java)
        TableUtils.createTableIfNotExists(it, Appointment::class.java)
    }
}

fun getOpenApiPluginOptions(): OpenApiOptions {
    val applicationInfo: Info = Info()
        .version("1.0")
        .description("Učionice")
    return OpenApiOptions(applicationInfo)
        .path("/swagger-docs")
        .swagger(SwaggerOptions("/swagger").title("Učionice Documentation"))
}

fun main(args: Array<String>) {
    setupDatabase()
    JavalinJackson.configure(ObjectMapper().registerModule(KotlinModule()))
    Javalin
        .create {
            it.accessManager(::authorizeRequest)
            it.contextPath = "/ucionice"
            it.registerPlugin(OpenApiPlugin(getOpenApiPluginOptions()))
            it.sessionHandler { fileSessionHandler() }
            it.enableCorsForAllOrigins()
        }
        .routes(
            AuthenticationController(),
            AppointmentController(),
            ClassroomController(),
            GroupController(),
            GroupSettingsController()
        )
        .exception(AuthenticationFailedException::class.java) { _, ctx ->
            ctx.status(HttpStatus.BAD_REQUEST_400).result("Authentication failed")
        }
        .exception(AuthorizationFailedException::class.java) { _, ctx ->
            ctx.status(HttpStatus.UNAUTHORIZED_401).result("Unauthorized")
        }
        .exception(TokenExpiredException::class.java) { _, ctx ->
            ctx.status(HttpStatus.UNAUTHORIZED_401).result("Token has expired, please login again")
        }
        .exception(UserExistsException::class.java) { e, ctx ->
            ctx.status(HttpStatus.BAD_REQUEST_400).result("User with email ${e.email} already exists")
        }
        .exception(GroupAuthorizationFailedException::class.java) { e, ctx ->
            ctx.status(HttpStatus.BAD_REQUEST_400).result("User doesn't have access to this group")
        }
        .exception(RoleAuthorizationFailedException::class.java) { e, ctx ->
            ctx.status(HttpStatus.BAD_REQUEST_400).result("User doesn't have sufficient role to access this route")
        }
        .exception(ConflictingAppointmentsException::class.java) { e, ctx ->
            ctx.status(HttpStatus.BAD_REQUEST_400)
                .result("Appointment conflicts with other already approved appointments")
        }
        .start(5005)
}

enum class UserRole : Role {
    ANYONE, OWNER, MANAGER, USER
}
