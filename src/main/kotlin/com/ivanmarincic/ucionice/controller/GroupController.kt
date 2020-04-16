package com.ivanmarincic.ucionice.controller

import com.ivanmarincic.ucionice.UserRole
import com.ivanmarincic.ucionice.model.Appointment
import com.ivanmarincic.ucionice.model.Classroom
import com.ivanmarincic.ucionice.model.Group
import com.ivanmarincic.ucionice.model.GroupUser
import com.ivanmarincic.ucionice.service.ClassroomsService
import com.ivanmarincic.ucionice.service.GroupsService
import com.ivanmarincic.ucionice.util.authenticatedUser
import io.javalin.apibuilder.ApiBuilder.*
import io.javalin.apibuilder.EndpointGroup
import io.javalin.http.Context
import io.javalin.plugin.openapi.annotations.OpenApi
import io.javalin.plugin.openapi.annotations.OpenApiContent
import io.javalin.plugin.openapi.annotations.OpenApiRequestBody
import io.javalin.plugin.openapi.annotations.OpenApiResponse

class GroupController : EndpointGroup {

    private val groupsService by lazy {
        GroupsService()
    }

    override fun addEndpoints() {
        path("/groups") {
            get("/all", ::all, setOf(UserRole.ANYONE))
            post("/select", ::select, setOf(UserRole.ANYONE))
            post("/create", ::create, setOf(UserRole.ANYONE))
            get("/delete", ::delete, setOf(UserRole.OWNER))
        }
    }

    @OpenApi(
        requestBody = OpenApiRequestBody([OpenApiContent(Group::class)]),
        responses = [OpenApiResponse("200", [OpenApiContent(GroupUser::class)])],
        description = "Selects group and returns settings for it"
    )
    private fun all(ctx: Context) {
        val userGroup = groupsService.allForUser(ctx.authenticatedUser()!!)
        ctx.sessionAttribute("group", userGroup)
        ctx.json(userGroup)
    }

    @OpenApi(
        requestBody = OpenApiRequestBody([OpenApiContent(Group::class)]),
        responses = [OpenApiResponse("200", [OpenApiContent(GroupUser::class)])],
        description = "Selects group and returns settings for it"
    )
    private fun select(ctx: Context) {
        val userGroup = groupsService.create(ctx.bodyValidator(Group::class.java).get(), ctx.authenticatedUser()!!)
        ctx.sessionAttribute("group", userGroup)
        ctx.json(userGroup)
    }

    @OpenApi(
        requestBody = OpenApiRequestBody([OpenApiContent(Group::class)]),
        description = "Creates new group"
    )
    private fun create(ctx: Context) {
    }

    @OpenApi(
        requestBody = OpenApiRequestBody([OpenApiContent(Group::class)]),
        description = "Deletes existing group"
    )
    private fun delete(ctx: Context) {
        ctx.sessionAttribute("group", ctx.bodyValidator(Group::class.java).get())
    }
}
