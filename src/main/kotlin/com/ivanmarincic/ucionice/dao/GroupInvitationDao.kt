package com.ivanmarincic.ucionice.dao

import com.ivanmarincic.ucionice.model.GroupInvitation
import com.j256.ormlite.dao.BaseDaoImpl
import com.j256.ormlite.support.ConnectionSource

class GroupInvitationDao(connectionSource: ConnectionSource) :
    BaseDaoImpl<GroupInvitation, Int>(connectionSource, GroupInvitation::class.java) {

    fun getByToken(token: String): GroupInvitation? {
        return queryBuilder()
            .where()
            .eq("token", token)
            .queryForFirst()
    }
}
