package com.ivanmarincic.ucionice.model

import com.ivanmarincic.ucionice.dao.ClassroomDao
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable

@DatabaseTable(tableName = "classrooms", daoClass = ClassroomDao::class)
data class Classroom(
    @DatabaseField(index = true, generatedId = true)
    var id: Int = 0,
    @DatabaseField
    var name: String = "",
    @DatabaseField(foreign = true)
    var group: Group = Group(),
    @DatabaseField
    var capacity: Int = 0,
    @DatabaseField
    var computer: Boolean = false,
    @DatabaseField
    var projector: Boolean = false,
    @DatabaseField
    var deleted: Boolean = false
)
