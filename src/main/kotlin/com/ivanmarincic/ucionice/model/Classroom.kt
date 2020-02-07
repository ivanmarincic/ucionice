package com.ivanmarincic.ucionice.model

import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable

@DatabaseTable(tableName = "classrooms")
data class Classroom(
    @DatabaseField(index = true, generatedId = true)
    var id: Int = 0,
    @DatabaseField
    var name: String = "",
    @DatabaseField(foreign = true)
    var group: Group = Group()
)
