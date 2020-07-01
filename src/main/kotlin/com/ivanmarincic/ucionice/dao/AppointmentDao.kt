package com.ivanmarincic.ucionice.dao

import com.ivanmarincic.ucionice.model.Appointment
import com.j256.ormlite.dao.BaseDaoImpl
import com.j256.ormlite.support.ConnectionSource
import java.util.*

class AppointmentDao(connectionSource: ConnectionSource) :
    BaseDaoImpl<Appointment, Int>(connectionSource, Appointment::class.java) {

    fun getUnapproved(): List<Appointment> {
        return queryBuilder()
            .orderBy("start_date", true)
            .where()
            .eq("approved", false)
            .query()
    }

    fun getOngoing(): List<Appointment> {
        val now = Date()
        return queryBuilder()
            .orderBy("start_date", true)
            .where()
            .ge("start_date", now)
            .or()
            .ge("end_date", now)
            .query()
    }

    fun getOngoingByClassroom(classroom: Int): List<Appointment> {
        val now = Date()
        return queryBuilder()
            .orderBy("start_date", true)
            .where()
            .ge("start_date", now)
            .and()
            .eq("classroom_id", classroom)
            .query()
    }

    fun getOngoingByUser(user: Int): List<Appointment> {
        val now = Date()
        return queryBuilder()
            .orderBy("start_date", true)
            .where()
            .ge("start_date", now)
            .and()
            .eq("user_id", user)
            .query()
    }

    fun getConflictingAppointments(startDate: Date, endDate: Date): List<Appointment> {
        val query = queryBuilder()
            .orderBy("start_date", true)
        val where = query.where()
        where.eq("approved", true)
        where.and(
            where,
            where.or(
                where.ge("start_date", startDate).and().le("end_date", endDate),
                where.ge("start_date", endDate).and().le("end_date", endDate)
            )
        )
        return query.query()
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
