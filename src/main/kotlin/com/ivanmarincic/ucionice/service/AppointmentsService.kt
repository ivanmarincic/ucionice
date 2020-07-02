package com.ivanmarincic.ucionice.service

import com.ivanmarincic.ucionice.Application
import com.ivanmarincic.ucionice.dao.AppointmentDao
import com.ivanmarincic.ucionice.dao.ClassroomDao
import com.ivanmarincic.ucionice.model.*
import com.ivanmarincic.ucionice.util.exceptions.ConflictingAppointmentsException
import com.j256.ormlite.dao.DaoManager

class AppointmentsService {
    private val classroomDao: ClassroomDao =
        DaoManager.createDao(Application.connectionSource, Classroom::class.java)
    private val appointmentDao: AppointmentDao =
        DaoManager.createDao(Application.connectionSource, Appointment::class.java)

    init {
        appointmentDao.classroomDao = classroomDao
    }

    fun getUnapproved(group: Group): List<Appointment> {
        return appointmentDao.getUnapproved(group.id)
    }

    fun request(appointmentRequest: AppointmentRequest, user: User): Appointment {
        val appointment = Appointment(
            classroom = appointmentRequest.classroom,
            startDate = appointmentRequest.startDate,
            endDate = appointmentRequest.endDate,
            user = user
        )
        return appointmentDao.request(appointment)
    }

    fun approve(appointment: Appointment): Appointment {
        if (!appointmentDao.hasConflictingAppointments(
                appointment.startDate,
                appointment.endDate,
                appointment.classroom.id
            )
        ) {
            return appointmentDao.approve(appointment)
        } else {
            throw ConflictingAppointmentsException()
        }
    }

    fun cancel(appointment: Appointment): Appointment {
        return appointmentDao.cancel(appointment)
    }

    fun move(appointmentMoveRequest: AppointmentMoveRequest): Appointment {
        val appointment = appointmentMoveRequest.appointment
        if (!appointment.approved) {
            appointment.startDate = appointmentMoveRequest.startDate
            appointment.endDate = appointmentMoveRequest.endDate
            appointmentDao.update(appointment)
            return appointment
        }
        throw ConflictingAppointmentsException()
    }

    fun getOngoing(group: Group): List<Appointment> {
        return appointmentDao.getOngoing(group.id)
    }

    fun getAll(group: Group): List<Appointment> {
        return appointmentDao.queryForAll(group.id)
    }

    fun getOngoingByClassroom(classroom: Int): List<Appointment> {
        return appointmentDao.getOngoingByClassroom(classroom)
    }

    fun getOngoingByUser(user: Int, group: Group): List<Appointment> {
        return appointmentDao.getOngoingByUser(user, group.id)
    }
}
