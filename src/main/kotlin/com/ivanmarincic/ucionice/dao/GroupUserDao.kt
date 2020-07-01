package com.ivanmarincic.ucionice.dao

import com.ivanmarincic.ucionice.UserRole
import com.ivanmarincic.ucionice.model.GroupUser
import com.ivanmarincic.ucionice.model.User
import com.j256.ormlite.dao.BaseDaoImpl
import com.j256.ormlite.support.ConnectionSource

class GroupUserDao(connectionSource: ConnectionSource) :
    BaseDaoImpl<GroupUser, Int>(connectionSource, GroupUser::class.java) {

    fun getByGroupAndUser(group: Int, user: Int): GroupUser? {
        return queryBuilder()
            .where()
            .eq("user_id", user)
            .and()
            .eq("group_id", group)
            .queryForFirst()
    }

    fun allForUser(user: User): List<GroupUser> {
        return queryBuilder()
            .where()
            .eq("user_id", user)
            .query()
    }

    fun allForGroup(group: Int): List<GroupUser> {
        return queryBuilder()
            .where()
            .eq("group_id", group)
            .and()
            .ne("role", UserRole.OWNER)
            .query()
    }

    fun removeFromGroup(user: Int, group: Int) {
        val builder = deleteBuilder()
        val where = builder.where()
            .eq("user_id", user)
            .and()
            .eq("group_id", group)
        builder.setWhere(where)
        builder.delete()
    }

    fun setRole(role: UserRole, user: Int, group: Int): GroupUser {
        val builder = updateBuilder()
            .updateColumnValue("role", role)
        val where = builder
            .where()
            .eq("user_id", user)
            .and()
            .eq("group_id", group)
        builder.setWhere(where)
        builder.update()
        return queryBuilder()
            .where()
            .eq("user_id", user)
            .and()
            .eq("group_id", group)
            .queryForFirst()
    }
}
