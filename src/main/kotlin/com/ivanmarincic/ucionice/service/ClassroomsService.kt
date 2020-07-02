package com.ivanmarincic.ucionice.service

import com.ivanmarincic.ucionice.Application
import com.ivanmarincic.ucionice.dao.ClassroomDao
import com.ivanmarincic.ucionice.model.Classroom
import com.ivanmarincic.ucionice.model.Group
import com.j256.ormlite.dao.DaoManager

class ClassroomsService {
    private val classroomDao: ClassroomDao =
        DaoManager.createDao(Application.connectionSource, Classroom::class.java)

    fun add(classroom: Classroom): Classroom {
        classroomDao.create(classroom)
        return classroom
    }

    fun remove(classroom: Classroom): Boolean {
        return classroomDao.delete(classroom) == 1
    }

    fun update(classroom: Classroom): Classroom {
        classroomDao.update(classroom)
        return classroom
    }

    fun getByGroup(group: Group): List<Classroom> {
        return classroomDao.getByGroup(group.id)
    }
}
