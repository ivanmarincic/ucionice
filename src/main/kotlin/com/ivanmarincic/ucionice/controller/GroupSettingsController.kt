package com.ivanmarincic.ucionice.controller

import com.ivanmarincic.ucionice.UserRole
import com.ivanmarincic.ucionice.model.Classroom
import com.ivanmarincic.ucionice.model.Group
import com.ivanmarincic.ucionice.model.GroupUser
import com.ivanmarincic.ucionice.service.GroupSettingsService
import io.javalin.apibuilder.ApiBuilder.*
import io.javalin.apibuilder.EndpointGroup
import io.javalin.http.Context
import io.javalin.plugin.openapi.annotations.OpenApi
import io.javalin.plugin.openapi.annotations.OpenApiContent
import io.javalin.plugin.openapi.annotations.OpenApiRequestBody
import io.javalin.plugin.openapi.annotations.OpenApiResponse

class GroupSettingsController : EndpointGroup {

    private val groupSettingsService by lazy {
        GroupSettingsService()
    }

    override fun addEndpoints() {
        path("/group-settings") {
            get("/get", ::get, setOf(UserRole.OWNER))
            post("/update", ::update, setOf(UserRole.OWNER))
        }
    }

    @OpenApi(
        requestBody = OpenApiRequestBody([OpenApiContent(Classroom::class)]),
        responses = [OpenApiResponse("200", [OpenApiContent(Classroom::class)])],
        description = "Gets settings for current group"

    )
    private fun get(ctx: Context) {
        ctx.json(groupSettingsService.get(ctx.attribute<GroupUser>("group")?.group!!))
    }

    @OpenApi(
        requestBody = OpenApiRequestBody([OpenApiContent(Classroom::class)]),
        responses = [OpenApiResponse("200", [OpenApiContent(Classroom::class)])],
        description = "Update group settings"
    )
    private fun update(ctx: Context) {
        ctx.json(groupSettingsService.update(ctx.bodyValidator(Group::class.java).get()))
    }
}
