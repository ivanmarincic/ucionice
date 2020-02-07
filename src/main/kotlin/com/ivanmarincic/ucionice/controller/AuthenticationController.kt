package com.ivanmarincic.ucionice.controller

import com.ivanmarincic.ucionice.model.AuthenticationRequest
import com.ivanmarincic.ucionice.model.User
import com.ivanmarincic.ucionice.service.UsersService
import io.javalin.apibuilder.ApiBuilder.path
import io.javalin.apibuilder.ApiBuilder.post
import io.javalin.apibuilder.EndpointGroup
import io.javalin.http.Context

class AuthenticationController : EndpointGroup {

    private val usersService by lazy {
        UsersService()
    }

    override fun addEndpoints() {
        path("/auth") {
            post("/login", ::login)
            post("/register", ::register)
        }
    }

    private fun login(ctx: Context) {
        ctx.json(usersService.login(ctx.bodyValidator<AuthenticationRequest>().get()))
    }

    private fun register(ctx: Context) {
        ctx.json(
            usersService
                .register(
                    ctx
                        .bodyValidator<User>()
                        .get()
                )
        )
    }
}
