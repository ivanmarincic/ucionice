package com.ivanmarincic.ucionice.controller

import com.ivanmarincic.ucionice.UserRole
import com.ivanmarincic.ucionice.model.Appointment
import com.ivanmarincic.ucionice.model.AppointmentMoveRequest
import com.ivanmarincic.ucionice.model.AppointmentRequest
import com.ivanmarincic.ucionice.service.AppointmentsService
import com.ivanmarincic.ucionice.util.authenticatedUser
import io.javalin.apibuilder.ApiBuilder.*
import io.javalin.apibuilder.EndpointGroup
import io.javalin.http.Context
import io.javalin.plugin.openapi.annotations.*

class AppointmentController : EndpointGroup {

    private val appointmentService by lazy {
        AppointmentsService()
    }

    override fun addEndpoints() {
        path("/appointments") {
            post("/request", ::request, setOf(UserRole.USER))
            post("/approve", ::approve, setOf(UserRole.MANAGER, UserRole.OWNER))
            post("/move", ::move, setOf(UserRole.MANAGER, UserRole.OWNER))
            get("/ongoing", ::ongoing, setOf(UserRole.USER, UserRole.MANAGER, UserRole.OWNER))
            get("/classroom/:id", ::classroom, setOf(UserRole.USER))
            get("/all", ::all, setOf(UserRole.MANAGER, UserRole.OWNER))
            get("/unapproved", ::unapproved, setOf(UserRole.MANAGER, UserRole.OWNER))
        }
    }

    @OpenApi(
        requestBody = OpenApiRequestBody([OpenApiContent(AppointmentRequest::class)]),
        responses = [OpenApiResponse("200", [OpenApiContent(Appointment::class)])],
        description = "Send request for new appointment"
    )
    private fun request(ctx: Context) {
        ctx.json(
            appointmentService.request(
                ctx.bodyValidator(AppointmentRequest::class.java).get(),
                ctx.authenticatedUser()!!
            )
        )
    }

    @OpenApi(
        requestBody = OpenApiRequestBody([OpenApiContent(Appointment::class)]),
        responses = [OpenApiResponse("200", [OpenApiContent(Appointment::class)])],
        description = "Approves appointment request"
    )
    private fun approve(ctx: Context) {
        ctx.json(appointmentService.approve(ctx.bodyValidator(Appointment::class.java).get()))
    }

    @OpenApi(
        requestBody = OpenApiRequestBody([OpenApiContent(AppointmentMoveRequest::class)]),
        responses = [OpenApiResponse("200", [OpenApiContent(Appointment::class)])],
        description = "Move appointment to another date and time if its conflicting with other"
    )
    private fun move(ctx: Context) {
        ctx.json(appointmentService.move(ctx.bodyValidator(AppointmentMoveRequest::class.java).get()))
    }

    @OpenApi(
        responses = [OpenApiResponse("200", [OpenApiContent(Appointment::class, true)])],
        description = "Returns list of all future appointments"
    )
    private fun ongoing(ctx: Context) {
        ctx.json(appointmentService.getOngoing())
    }

    @OpenApi(
        queryParams = [OpenApiParam("id", Int::class, "Classroom id")],
        responses = [OpenApiResponse("200", [OpenApiContent(Appointment::class, true)])],
        description = "Returns list of all future appointments for classroom"
    )
    private fun classroom(ctx: Context) {
        ctx.json(appointmentService.getOngoingByClassroom(ctx.pathParam<Int>("id").get()))
    }


    @OpenApi(
        responses = [OpenApiResponse("200", [OpenApiContent(Appointment::class, true)])],
        description = "Returns list of all appointments"
    )
    private fun all(ctx: Context) {
        ctx.json(appointmentService.getAll())
    }

    @OpenApi(
        responses = [OpenApiResponse("200", [OpenApiContent(Appointment::class, true)])],
        description = "Returns list of all appointments that need approval"
    )
    private fun unapproved(ctx: Context) {
        ctx.json(appointmentService.getUnapproved())
    }
}
