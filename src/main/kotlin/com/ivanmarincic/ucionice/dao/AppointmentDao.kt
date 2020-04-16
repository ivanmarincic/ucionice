package com.ivanmarincic.ucionice.dao

import com.ivanmarincic.ucionice.model.Appointment
import com.j256.ormlite.dao.BaseDaoImpl
import com.j256.ormlite.support.ConnectionSource
import java.util.*

class AppointmentDao(connectionSource: ConnectionSource) :
    BaseDaoImpl<Appointment, Int>(connectionSource, Appointment::class.java) {

    fun getUnapproved(): List<Appointment> {
        return queryBuilder()
            .orderBy("startDate", true)
            .where()
            .eq("approved", false)
            .query()
    }

    fun getOngoing(): List<Appointment> {
        val now = Date()
        return queryBuilder()
            .orderBy("startDate", true)
            .where()
            .ge("startDate", now)
            .or()
            .ge("endDate", now)
            .query()
    }

    fun getOngoingByClassroom(classroom: Int): List<Appointment> {
        val now = Date()
        return queryBuilder()
            .orderBy("startDate", true)
            .where()
            .ge("startDate", now)
            .and()
            .eq("classroom_id", classroom)
            .query()
    }

    fun getConflictingAppointments(startDate: Date, endDate: Date): List<Appointment> {
        return queryBuilder()
            .orderBy("startDate", true)
            .where()
            .eq("approved", true)
            .or(queryBuilder().where().ge("startDate", startDate).and().le("endDate", endDate),
                queryBuilder().where().ge("startDate", endDate).and().le("endDate", endDate))
            .query()
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
}
