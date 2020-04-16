package com.ivanmarincic.ucionice.service

import com.ivanmarincic.ucionice.Application
import com.ivanmarincic.ucionice.dao.ClassroomDao
import com.ivanmarincic.ucionice.model.Classroom
import com.ivanmarincic.ucionice.model.Group
import com.ivanmarincic.ucionice.util.futureOf
import com.j256.ormlite.dao.DaoManager
import java.util.concurrent.CompletableFuture
import java.util.function.Supplier

class ClassroomsService {
    private val classroomDao: ClassroomDao =
        DaoManager.createDao(Application.connectionSource, Classroom::class.java)

    fun add(classroom: Classroom): CompletableFuture<Classroom> {
        return futureOf(Supplier {
            classroomDao.create(classroom)
            classroom
        })
    }

    fun remove(classroom: Classroom): CompletableFuture<Boolean> {
        return futureOf(Supplier {
            classroomDao.delete(classroom) == 1
        })
    }

    fun update(classroom: Classroom): CompletableFuture<Classroom> {
        return futureOf(Supplier {
            classroomDao.update(classroom)
            classroom
        })
    }

    fun getByGroup(group: Group): CompletableFuture<List<Classroom>> {
        return futureOf(Supplier {
            classroomDao.getByGroup(group.id)
        })
    }
}
