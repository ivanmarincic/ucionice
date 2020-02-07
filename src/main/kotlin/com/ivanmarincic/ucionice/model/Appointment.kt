package com.ivanmarincic.ucionice.model

import com.ivanmarincic.ucionice.dao.UsersDao
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable
import java.util.*

@DatabaseTable(tableName = "appointments")
data class Appointment(
    @DatabaseField(index = true, generatedId = true)
    var id: Int = 0,
    @DatabaseField(foreign = true)
    var classroom: Classroom = Classroom(),
    @DatabaseField(foreign = true)
    var user: User = User(),
    @DatabaseField
    var startDate: Date = Date(),
    @DatabaseField
    var endDate: Date = Date(),
    @DatabaseField
    var approved: Boolean = false
)
