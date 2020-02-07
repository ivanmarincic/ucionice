package com.ivanmarincic.ucionice.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.ivanmarincic.ucionice.dao.UsersDao
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable

@DatabaseTable(tableName = "users", daoClass = UsersDao::class)
data class User(
    @DatabaseField(index = true, generatedId = true)
    var id: Int = 0,
    @DatabaseField(unique = true)
    var email: String = "",
    @DatabaseField
    var name: String = "",
    @DatabaseField
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    var password: String = ""
)
