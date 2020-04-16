package com.ivanmarincic.ucionice.dao

import com.ivanmarincic.ucionice.model.Classroom
import com.ivanmarincic.ucionice.model.Group
import com.j256.ormlite.dao.BaseDaoImpl
import com.j256.ormlite.support.ConnectionSource

class ClassroomDao(connectionSource: ConnectionSource) :
    BaseDaoImpl<Classroom, Int>(connectionSource, Classroom::class.java) {

    fun getByGroup(group: Int): List<Classroom> {
        return queryBuilder()
            .where()
            .eq("group_id", group)
            .query()
    }
}
