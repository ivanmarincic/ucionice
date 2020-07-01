package com.ivanmarincic.ucionice.controller

import com.ivanmarincic.ucionice.UserRole
import com.ivanmarincic.ucionice.model.*
import com.ivanmarincic.ucionice.service.GroupsService
import com.ivanmarincic.ucionice.util.authenticatedUser
import com.ivanmarincic.ucionice.util.selectedGroup
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
            post("/invite", ::invite, setOf(UserRole.OWNER))
            get("/users", ::users, setOf(UserRole.OWNER))
            post("/users/remove", ::usersRemove, setOf(UserRole.OWNER))
            post("/users/role", ::usersRole, setOf(UserRole.OWNER))
            post("/accept", ::accept, setOf(UserRole.ANYONE))
            post("/leave", ::leave, setOf(UserRole.USER, UserRole.MANAGER))
            get("/invitations", ::invitations, setOf(UserRole.ANYONE))
        }
    }

    @OpenApi(
        requestBody = OpenApiRequestBody([OpenApiContent(Group::class)]),
        responses = [OpenApiResponse("200", [OpenApiContent(GroupUser::class)])],
        description = "Selects group and returns settings for it"
    )
    private fun all(ctx: Context) {
        val userGroup = groupsService.allForUser(ctx.authenticatedUser()!!)
        ctx.json(userGroup)
    }

    @OpenApi(
        requestBody = OpenApiRequestBody([OpenApiContent(Group::class)]),
        responses = [OpenApiResponse("200", [OpenApiContent(GroupUser::class)])],
        description = "Selects group and returns settings for it"
    )
    private fun select(ctx: Context) {
        ctx.sessionAttribute("group", ctx.bodyValidator(GroupUser::class.java).get())
    }

    @OpenApi(
        requestBody = OpenApiRequestBody([OpenApiContent(Group::class)]),
        description = "Creates new group"
    )
    private fun create(ctx: Context) {
        val userGroup = groupsService.create(ctx.bodyValidator(Group::class.java).get(), ctx.authenticatedUser()!!)
        ctx.sessionAttribute("group", userGroup)
        ctx.json(userGroup)
    }

    @OpenApi(
        requestBody = OpenApiRequestBody([OpenApiContent(Group::class)]),
        description = "Deletes existing group"
    )
    private fun delete(ctx: Context) {
        ctx.sessionAttribute("group", ctx.bodyValidator(Group::class.java).get())
    }

    @OpenApi(
        requestBody = OpenApiRequestBody([OpenApiContent(Group::class)]),
        description = "Invites user to group"
    )
    private fun invite(ctx: Context) {
        groupsService.createInvitation(ctx.selectedGroup()!!.group, ctx.bodyValidator(User::class.java).get())
    }

    @OpenApi(
        requestBody = OpenApiRequestBody([OpenApiContent(GroupUser::class, true)]),
        description = "Returns list of all users for group"
    )
    private fun users(ctx: Context) {
        ctx.json(groupsService.getByUsersByGroup(ctx.selectedGroup()!!.group))
    }

    @OpenApi(
        requestBody = OpenApiRequestBody([OpenApiContent(Group::class)]),
        description = "Removes user from group"
    )
    private fun usersRemove(ctx: Context) {
        groupsService.removeUser(ctx.bodyValidator(User::class.java).get(), ctx.selectedGroup()!!.group)
    }

    @OpenApi(
        requestBody = OpenApiRequestBody([OpenApiContent(Group::class)]),
        description = "Set user role"
    )
    private fun usersRole(ctx: Context) {
        ctx.json(
            groupsService.setUserRole(
                ctx.bodyValidator(RoleRequest::class.java).get(),
                ctx.selectedGroup()!!.group
            )
        )
    }

    @OpenApi(
        requestBody = OpenApiRequestBody([OpenApiContent(Group::class)]),
        description = "Accept invitation to group"
    )
    private fun accept(ctx: Context) {
        ctx.bodyValidator(GroupInvitation::class.java).get().let {
            groupsService.acceptInvitation(it.token, ctx.authenticatedUser()!!)
        }
    }

    @OpenApi(
        requestBody = OpenApiRequestBody([OpenApiContent(Group::class)]),
        description = "Leave group"
    )
    private fun leave(ctx: Context) {
        ctx.json(groupsService.leaveGroup(ctx.selectedGroup()!!))
    }

    @OpenApi(
        requestBody = OpenApiRequestBody([OpenApiContent(GroupInvitation::class, true)]),
        description = "Returns list of all invitations for user"
    )
    private fun invitations(ctx: Context) {
        ctx.json(groupsService.userInvitations(ctx.authenticatedUser()!!))
    }
}
