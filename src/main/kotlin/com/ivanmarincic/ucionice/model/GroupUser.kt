package com.ivanmarincic.ucionice.model

import com.ivanmarincic.ucionice.UserRole
import com.ivanmarincic.ucionice.dao.GroupUserDao
import com.ivanmarincic.ucionice.dao.UsersDao
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.field.types.EnumIntegerType
import com.j256.ormlite.table.DatabaseTable

@DatabaseTable(tableName = "group_users", daoClass = GroupUserDao::class)
data class GroupUser(
    @DatabaseField(index = true, generatedId = true)
    var id: Int = 0,
    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    var group: Group = Group(),
    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    var user: User = User(),
    @DatabaseField(persisterClass = EnumIntegerType::class)
    var role: UserRole = UserRole.ANYONE
)
