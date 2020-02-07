package com.ivanmarincic.ucionice.model

import com.ivanmarincic.ucionice.dao.UsersDao
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable

@DatabaseTable(tableName = "group_invitations")
data class GroupInvitation(
    @DatabaseField(index = true, generatedId = true)
    var id: Int = 0,
    @DatabaseField(foreign = true)
    var group: Group = Group(),
    @DatabaseField(foreign = true)
    var user: User = User(),
    @DatabaseField
    var token: String = ""
)
