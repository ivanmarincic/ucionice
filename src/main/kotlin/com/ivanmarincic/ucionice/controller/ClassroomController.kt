package com.ivanmarincic.ucionice.controller

import com.ivanmarincic.ucionice.UserRole
import com.ivanmarincic.ucionice.model.Classroom
import com.ivanmarincic.ucionice.model.GroupUser
import com.ivanmarincic.ucionice.service.ClassroomsService
import io.javalin.apibuilder.ApiBuilder.*
import io.javalin.apibuilder.EndpointGroup
import io.javalin.http.Context
import io.javalin.plugin.openapi.annotations.OpenApi
import io.javalin.plugin.openapi.annotations.OpenApiContent
import io.javalin.plugin.openapi.annotations.OpenApiRequestBody
import io.javalin.plugin.openapi.annotations.OpenApiResponse

class ClassroomController : EndpointGroup {

    private val classroomService by lazy {
        ClassroomsService()
    }

    override fun addEndpoints() {
        path("/classrooms") {
            post("/add", ::add, setOf(UserRole.OWNER))
            post("/remove", ::remove, setOf(UserRole.OWNER))
            post("/update", ::update, setOf(UserRole.OWNER))
            get("/all", ::all, setOf(UserRole.OWNER, UserRole.MANAGER, UserRole.USER))
        }
    }

    @OpenApi(
        requestBody = OpenApiRequestBody([OpenApiContent(Classroom::class)]),
        responses = [OpenApiResponse("200", [OpenApiContent(Classroom::class)])],
        description = "Adds new classroom"

    )
    private fun add(ctx: Context) {
        ctx.json(classroomService.add(ctx.bodyValidator(Classroom::class.java).get()))
    }

    @OpenApi(
        requestBody = OpenApiRequestBody([OpenApiContent(Classroom::class)]),
        responses = [OpenApiResponse("200", [OpenApiContent(Boolean::class)])],
        description = "Removes existing classroom"
    )
    private fun remove(ctx: Context) {
        if (classroomService.remove(ctx.bodyValidator(Classroom::class.java).get())) {
            ctx.status(200)
        } else {
            ctx.status(400)
        }
    }

    @OpenApi(
        requestBody = OpenApiRequestBody([OpenApiContent(Classroom::class)]),
        responses = [OpenApiResponse("200", [OpenApiContent(Classroom::class)])],
        description = "Updates existing classroom"
    )
    private fun update(ctx: Context) {
        ctx.json(classroomService.update(ctx.bodyValidator(Classroom::class.java).get()))
    }

    @OpenApi(
        responses = [OpenApiResponse("200", [OpenApiContent(Classroom::class, true)])],
        description = "Returns list of all classrooms for group"
    )
    private fun all(ctx: Context) {
        ctx.json(classroomService.getByGroup(ctx.attribute<GroupUser>("group")?.group!!))
    }
}
