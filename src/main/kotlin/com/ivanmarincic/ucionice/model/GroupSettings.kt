package com.ivanmarincic.ucionice.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.ivanmarincic.ucionice.UserRole
import com.ivanmarincic.ucionice.dao.UsersDao
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.field.types.EnumIntegerType
import com.j256.ormlite.table.DatabaseTable

@DatabaseTable(tableName = "group_settings")
data class GroupSettings(
    @DatabaseField(index = true, generatedId = true)
    var id: Int = 0,
    @DatabaseField
    var color: String = "",
    @DatabaseField
    var logo: String = ""
)
