package com.ivanmarincic.ucionice.service

import com.ivanmarincic.ucionice.Application
import com.ivanmarincic.ucionice.dao.GroupDao
import com.ivanmarincic.ucionice.model.Group
import com.ivanmarincic.ucionice.util.futureOf
import com.j256.ormlite.dao.DaoManager
import java.util.concurrent.CompletableFuture
import java.util.function.Supplier

class GroupSettingsService {
    private val groupSettingsDao: GroupDao =
        DaoManager.createDao(Application.connectionSource, Group::class.java)

    fun get(group: Group): CompletableFuture<Group> {
        return futureOf(Supplier {
            groupSettingsDao.queryForEq("group_id", group.id).first()
        })
    }

    fun update(settings: Group): CompletableFuture<Group> {
        return futureOf(Supplier {
            groupSettingsDao.updateBuilder()
                .updateColumnValue("color", settings.color)
                .updateColumnValue("logo", settings.logo)
                .update()
            settings
        })
    }
}
