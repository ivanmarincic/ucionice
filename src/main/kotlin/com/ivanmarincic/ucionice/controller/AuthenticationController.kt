package com.ivanmarincic.ucionice.controller

import com.ivanmarincic.ucionice.UserRole
import com.ivanmarincic.ucionice.model.AuthenticationRequest
import com.ivanmarincic.ucionice.model.AuthenticationResponse
import com.ivanmarincic.ucionice.model.User
import com.ivanmarincic.ucionice.service.UsersService
import com.ivanmarincic.ucionice.util.authenticatedUser
import com.ivanmarincic.ucionice.util.exceptions.AuthenticationFailedException
import com.ivanmarincic.ucionice.util.exceptions.AuthorizationFailedException
import io.javalin.apibuilder.ApiBuilder.path
import io.javalin.apibuilder.ApiBuilder.post
import io.javalin.apibuilder.EndpointGroup
import io.javalin.http.Context
import io.javalin.plugin.openapi.annotations.OpenApi
import io.javalin.plugin.openapi.annotations.OpenApiContent
import io.javalin.plugin.openapi.annotations.OpenApiRequestBody
import io.javalin.plugin.openapi.annotations.OpenApiResponse

class AuthenticationController : EndpointGroup {

    private val usersService by lazy {
        UsersService()
    }

    override fun addEndpoints() {
        path("/auth") {
            post("/login", ::login, setOf(UserRole.ANYONE))
            post("/register", ::register, setOf(UserRole.ANYONE))
            post("/validate", ::validate, setOf(UserRole.ANYONE))
        }
    }

    @OpenApi(
        requestBody = OpenApiRequestBody([OpenApiContent(AuthenticationRequest::class)]),
        responses = [OpenApiResponse("200", [OpenApiContent(AuthenticationResponse::class)])],
        description = "Used for logging in into the system"
    )
    private fun login(ctx: Context) {
        val auth = usersService.login(ctx.bodyValidator<AuthenticationRequest>().get())
        try {
            ctx.req.changeSessionId()
        } catch (e: Exception) {
        } finally {
            ctx.sessionAttribute("user", auth.user)
            ctx.sessionAttribute("expiration", auth.expiresAt)
        }
        ctx.json(auth)
    }

    @OpenApi(
        requestBody = OpenApiRequestBody([OpenApiContent(User::class)]),
        responses = [OpenApiResponse("200", [OpenApiContent(User::class)])],
        description = "Registers new user"
    )
    private fun register(ctx: Context) {
        ctx.json(usersService.register(ctx.bodyValidator<User>().get()))
    }

    @OpenApi(
        requestBody = OpenApiRequestBody([OpenApiContent(User::class)]),
        responses = [OpenApiResponse("200", [OpenApiContent(User::class)])],
        description = "Validates access token"
    )
    private fun validate(ctx: Context) {
        ctx.authenticatedUser()?.let {
            ctx.json(it)
        } ?: throw AuthorizationFailedException()
    }
}
