package com.ivanmarincic.ucionice.dao

import com.ivanmarincic.ucionice.model.Appointment
import com.j256.ormlite.dao.BaseDaoImpl
import com.j256.ormlite.support.ConnectionSource
import java.util.*

class AppointmentDao(connectionSource: ConnectionSource) :
    BaseDaoImpl<Appointment, Int>(connectionSource, Appointment::class.java) {

    lateinit var classroomDao: ClassroomDao

    fun queryForAll(group: Int): List<Appointment> {
        val classroomQuery = classroomDao.queryBuilder()
        classroomQuery.where().eq("group_id", group)
        return queryBuilder()
            .join(classroomQuery)
            .query()

    }

    fun getUnapproved(group: Int): List<Appointment> {
        val now = Date()
        val classroomQuery = classroomDao.queryBuilder()
        classroomQuery.where().eq("group_id", group)
        return queryBuilder()
            .join(classroomQuery)
            .orderBy("start_date", true)
            .where()
            .ge("start_date", now)
            .and()
            .eq("approved", true)
            .query()
    }

    fun getOngoing(group: Int): List<Appointment> {
        val now = Date()
        val classroomQuery = classroomDao.queryBuilder()
        classroomQuery.where().eq("group_id", group)
        return queryBuilder()
            .join(classroomQuery)
            .orderBy("start_date", true)
            .where()
            .ge("start_date", now)
            .and()
            .eq("approved", true)
            .query()
    }

    fun getOngoingByClassroom(classroom: Int): List<Appointment> {
        val now = Date()
        return queryBuilder()
            .orderBy("start_date", true)
            .where()
            .ge("start_date", now)
            .and()
            .eq("approved", true)
            .and()
            .eq("classroom_id", classroom)
            .query()
    }

    fun getOngoingByUser(user: Int, group: Int): List<Appointment> {
        val now = Date()
        val classroomQuery = classroomDao.queryBuilder()
        classroomQuery.where().eq("group_id", group)
        return queryBuilder()
            .join(classroomQuery)
            .orderBy("start_date", true)
            .where()
            .ge("start_date", now)
            .and()
            .eq("user_id", user)
            .query()
    }

    fun hasConflictingAppointments(startDate: Date, endDate: Date, classroom: Int): Boolean {
        val query = queryBuilder()
            .orderBy("start_date", true)
        val where = query.where()
        where
            .eq("approved", true)
            .and()
            .eq("classroom_id", classroom)
            .and(
                where,
                where.or(
                    where.le("start_date", startDate).and().ge("end_date", startDate),
                    where.le("start_date", endDate).and().ge("end_date", endDate)
                )
            )
        return query.countOf() > 0
    }

    fun request(appointment: Appointment): Appointment {
        appointment.approved = false
        create(appointment)
        return appointment
    }

    fun approve(appointment: Appointment): Appointment {
        appointment.approved = true
        update(appointment)
        return appointment
    }

    fun cancel(appointment: Appointment): Appointment {
        appointment.approved = false
        update(appointment)
        return appointment
    }
}
