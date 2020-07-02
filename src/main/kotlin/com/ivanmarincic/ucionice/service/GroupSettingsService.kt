package com.ivanmarincic.ucionice.service

import com.ivanmarincic.ucionice.Application
import com.ivanmarincic.ucionice.dao.GroupDao
import com.ivanmarincic.ucionice.model.Group
import com.j256.ormlite.dao.DaoManager

class GroupSettingsService {
    private val groupSettingsDao: GroupDao =
        DaoManager.createDao(Application.connectionSource, Group::class.java)

    fun get(group: Group): Group {
        return groupSettingsDao.queryForEq("group_id", group.id).first()
    }

    fun update(settings: Group): Group {
        groupSettingsDao.updateBuilder()
            .updateColumnValue("color", settings.color)
            .updateColumnValue("logo", settings.logo)
            .update()
        return settings
    }
}
