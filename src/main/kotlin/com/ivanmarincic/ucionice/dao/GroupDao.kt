package com.ivanmarincic.ucionice.dao

import com.ivanmarincic.ucionice.Application
import com.ivanmarincic.ucionice.model.Group
import com.ivanmarincic.ucionice.model.GroupUser
import com.ivanmarincic.ucionice.model.User
import com.j256.ormlite.dao.BaseDaoImpl
import com.j256.ormlite.dao.DaoManager
import com.j256.ormlite.support.ConnectionSource

class GroupDao(connectionSource: ConnectionSource) : BaseDaoImpl<Group, Int>(connectionSource, Group::class.java) {

    private val groupUserDao: GroupUserDao = DaoManager.createDao(Application.connectionSource, GroupUser::class.java)

    fun allForUser(user: User): List<Group> {
        val groupUsers = groupUserDao
            .queryBuilder()
        groupUsers.where()
            .eq("user_id", user.id)
        return queryBuilder()
            .join(groupUsers)
            .query()
    }
}
