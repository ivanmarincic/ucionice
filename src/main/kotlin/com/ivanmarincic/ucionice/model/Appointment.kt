package com.ivanmarincic.ucionice.model

import com.ivanmarincic.ucionice.dao.AppointmentDao
import com.j256.ormlite.field.DataType
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable
import java.util.*

@DatabaseTable(tableName = "appointments", daoClass = AppointmentDao::class)
data class Appointment(
    @DatabaseField(index = true, generatedId = true)
    var id: Int = 0,
    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    var classroom: Classroom = Classroom(),
    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    var user: User = User(),
    @DatabaseField(columnName = "start_date", dataType = DataType.DATE_STRING, format = "yyyy-MM-dd HH:mm:ss")
    var startDate: Date = Date(),
    @DatabaseField(columnName = "end_date", dataType = DataType.DATE_STRING, format = "yyyy-MM-dd HH:mm:ss")
    var endDate: Date = Date(),
    @DatabaseField
    var approved: Boolean = false,
    @DatabaseField
    var deleted: Boolean = false
)
