package com.ivanmarincic.ucionice.dao

import com.ivanmarincic.ucionice.model.GroupUser
import com.j256.ormlite.dao.BaseDaoImpl
import com.j256.ormlite.support.ConnectionSource

class GroupUserDao(connectionSource: ConnectionSource) :
    BaseDaoImpl<GroupUser, Int>(connectionSource, GroupUser::class.java) {

    fun getByGroupAndUser(group: Int, user: Int): GroupUser? {
        return queryBuilder()
            .where()
            .eq("user_id", user)
            .eq("group_id", group)
            .queryForFirst()
    }
}
