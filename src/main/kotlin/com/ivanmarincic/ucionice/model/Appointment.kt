package com.ivanmarincic.ucionice.model

import com.ivanmarincic.ucionice.dao.AppointmentDao
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable
import java.util.*

@DatabaseTable(tableName = "appointments", daoClass = AppointmentDao::class)
data class Appointment(
    @DatabaseField(index = true, generatedId = true)
    var id: Int = 0,
    @DatabaseField(foreign = true)
    var classroom: Classroom = Classroom(),
    @DatabaseField(foreign = true)
    var user: User = User(),
    @DatabaseField(columnName = "start_date")
    var startDate: Date = Date(),
    @DatabaseField(columnName = "end_date")
    var endDate: Date = Date(),
    @DatabaseField
    var approved: Boolean = false,
    @DatabaseField
    var deleted: Boolean = false
)
