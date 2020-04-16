package com.ivanmarincic.ucionice.model

import com.ivanmarincic.ucionice.dao.AppointmentDao
import com.ivanmarincic.ucionice.dao.UsersDao
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable
import java.util.*

data class AppointmentRequest(
    var classroom: Classroom = Classroom(),
    var startDate: Date = Date(),
    var endDate: Date = Date()
)
