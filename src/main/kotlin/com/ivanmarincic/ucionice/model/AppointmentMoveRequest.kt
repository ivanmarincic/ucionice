package com.ivanmarincic.ucionice.model

import java.util.*

data class AppointmentMoveRequest(
    var appointment: Appointment = Appointment(),
    var startDate: Date = Date(),
    var endDate: Date = Date()
)
