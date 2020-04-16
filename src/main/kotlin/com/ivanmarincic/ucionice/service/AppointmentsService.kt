package com.ivanmarincic.ucionice.service

import com.ivanmarincic.ucionice.Application
import com.ivanmarincic.ucionice.dao.AppointmentDao
import com.ivanmarincic.ucionice.model.Appointment
import com.ivanmarincic.ucionice.model.AppointmentMoveRequest
import com.ivanmarincic.ucionice.model.AppointmentRequest
import com.ivanmarincic.ucionice.model.User
import com.ivanmarincic.ucionice.util.exceptions.ConflictingAppointmentsException
import com.ivanmarincic.ucionice.util.futureOf
import com.j256.ormlite.dao.DaoManager
import java.util.concurrent.CompletableFuture
import java.util.function.Supplier

class AppointmentsService {
    private val appointmentDao: AppointmentDao =
        DaoManager.createDao(Application.connectionSource, Appointment::class.java)

    fun getUnapproved(): CompletableFuture<List<Appointment>> {
        return futureOf(Supplier {
            appointmentDao.getUnapproved()
        })
    }

    fun request(appointmentRequest: AppointmentRequest, user: User): CompletableFuture<Appointment> {
        return futureOf(Supplier {
            val appointment = Appointment(
                classroom = appointmentRequest.classroom,
                startDate = appointmentRequest.startDate,
                endDate = appointmentRequest.endDate,
                user = user
            )
            appointmentDao.request(appointment)
        })
    }

    fun approve(appointment: Appointment): CompletableFuture<Appointment> {
        return futureOf(Supplier {
            appointmentDao.approve(appointment)
        })
    }

    fun move(appointmentMoveRequest: AppointmentMoveRequest): CompletableFuture<Appointment> {
        return futureOf(Supplier {
            if (appointmentDao.getConflictingAppointments(
                    appointmentMoveRequest.startDate,
                    appointmentMoveRequest.endDate
                ).isEmpty()
            ) {
                val appointment = appointmentMoveRequest.appointment
                appointment.startDate = appointmentMoveRequest.startDate
                appointment.endDate = appointmentMoveRequest.endDate
                appointmentDao.update(appointment)
                appointment
            } else {
                throw ConflictingAppointmentsException()
            }
        })
    }

    fun getOngoing(): CompletableFuture<List<Appointment>> {
        return futureOf(Supplier {
            appointmentDao.getOngoing()
        })
    }

    fun getAll(): CompletableFuture<List<Appointment>> {
        return futureOf(Supplier {
            appointmentDao.queryForAll()
        })
    }

    fun getOngoingByClassroom(classroom: Int): CompletableFuture<List<Appointment>> {
        return futureOf(Supplier {
            appointmentDao.getOngoingByClassroom(classroom)
        })
    }
}
