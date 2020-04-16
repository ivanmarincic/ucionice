package com.ivanmarincic.ucionice.service

import com.ivanmarincic.ucionice.Application
import com.ivanmarincic.ucionice.UserRole
import com.ivanmarincic.ucionice.dao.GroupDao
import com.ivanmarincic.ucionice.dao.GroupInvitationDao
import com.ivanmarincic.ucionice.dao.GroupUserDao
import com.ivanmarincic.ucionice.model.*
import com.ivanmarincic.ucionice.util.exceptions.InvalidInvitationException
import com.ivanmarincic.ucionice.util.randomString
import com.j256.ormlite.dao.DaoManager

class GroupsService {
    private val groupsDao: GroupDao = DaoManager.createDao(Application.connectionSource, Group::class.java)
    private val groupUserDao: GroupUserDao = DaoManager.createDao(Application.connectionSource, GroupUser::class.java)
    private val groupInvitationDao: GroupInvitationDao =
        DaoManager.createDao(Application.connectionSource, GroupInvitation::class.java)

    fun allForUser(user: User): List<Group> {
        return groupsDao.allForUser(user)
    }

    fun create(group: Group, user: User): GroupUser {
        groupsDao.create(group)
        val groupUser = GroupUser(
            group = group,
            user = user,
            role = UserRole.OWNER
        )
        groupUserDao.create(groupUser)
        return groupUser
    }

    fun getByGroupAndUser(id: Int, user: User): GroupUser? {
        return groupUserDao
            .getByGroupAndUser(id, user.id)
    }

    fun createInvitation(group: Group, user: User): GroupInvitation {
        var token: String
        do {
            token = randomString(20)
        } while (groupInvitationDao.getByToken(token) != null)
        val invitation = GroupInvitation(
            group = group,
            user = user,
            token = token
        )
        groupInvitationDao.create(invitation)
        return invitation
    }

    fun acceptInvitation(token: String, user: User): GroupUser {
        val invitation = groupInvitationDao.getByToken(token)
        if (invitation != null && invitation.user == user) {
            val groupUser = GroupUser(
                user = user,
                group = invitation.group,
                role = UserRole.USER
            )
            groupUserDao.create(groupUser)
            return groupUser
        }
        throw InvalidInvitationException()
    }
}
