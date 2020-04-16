package com.ivanmarincic.ucionice.model

import com.ivanmarincic.ucionice.dao.GroupDao
import com.ivanmarincic.ucionice.dao.UsersDao
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable

@DatabaseTable(tableName = "groups", daoClass = GroupDao::class)
data class Group(
    @DatabaseField(index = true, generatedId = true)
    var id: Int = 0,
    @DatabaseField
    var name: String = "",
    @DatabaseField
    var color: String = "",
    @DatabaseField
    var logo: String = ""
)
