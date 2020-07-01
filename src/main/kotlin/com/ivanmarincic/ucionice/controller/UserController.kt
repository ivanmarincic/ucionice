package com.ivanmarincic.ucionice.controller

import com.ivanmarincic.ucionice.UserRole
import com.ivanmarincic.ucionice.model.Group
import com.ivanmarincic.ucionice.model.GroupInvitation
import com.ivanmarincic.ucionice.model.GroupUser
import com.ivanmarincic.ucionice.model.User
import com.ivanmarincic.ucionice.service.GroupsService
import com.ivanmarincic.ucionice.service.UsersService
import com.ivanmarincic.ucionice.util.authenticatedUser
import com.ivanmarincic.ucionice.util.selectedGroup
import io.javalin.apibuilder.ApiBuilder.*
import io.javalin.apibuilder.EndpointGroup
import io.javalin.http.Context
import io.javalin.plugin.openapi.annotations.OpenApi
import io.javalin.plugin.openapi.annotations.OpenApiContent
import io.javalin.plugin.openapi.annotations.OpenApiRequestBody
import io.javalin.plugin.openapi.annotations.OpenApiResponse

class UserController : EndpointGroup {

    private val usersService by lazy {
        UsersService()
    }

    override fun addEndpoints() {
        path("/users") {
            post("/find", ::find, setOf(UserRole.OWNER, UserRole.MANAGER))
        }
    }

    @OpenApi(
        requestBody = OpenApiRequestBody([OpenApiContent(User::class, true)]),
        description = "Find user to invite"
    )
    private fun find(ctx: Context) {
        ctx.json(usersService.findUsers(ctx.body(), ctx.selectedGroup()!!.group))
    }
}
