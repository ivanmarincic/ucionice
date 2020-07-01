package com.ivanmarincic.ucionice.service

import com.ivanmarincic.ucionice.Application
import com.ivanmarincic.ucionice.dao.AppointmentDao
import com.ivanmarincic.ucionice.dao.ClassroomDao
import com.ivanmarincic.ucionice.model.*
import com.ivanmarincic.ucionice.util.exceptions.ConflictingAppointmentsException
import com.ivanmarincic.ucionice.util.futureOf
import com.j256.ormlite.dao.DaoManager
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.function.Supplier

class AppointmentsService {
    private val appointmentDao: AppointmentDao =
        DaoManager.createDao(Application.connectionSource, Appointment::class.java)
    private val classroomDao: ClassroomDao =
        DaoManager.createDao(Application.connectionSource, Classroom::class.java)

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

    fun cancel(appointment: Appointment): CompletableFuture<Appointment> {
        return futureOf(Supplier {
            appointmentDao.cancel(appointment)
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

    fun getOngoing(group: Group): CompletableFuture<List<Appointment>> {
        return futureOf(Supplier {
            val now = Date()
            val classroomJoin = classroomDao.queryBuilder()
            classroomJoin.where().eq("group_id", group.id)
            val query = appointmentDao.queryBuilder().leftJoin(classroomJoin)
            query
                .orderBy("start_date", true)
                .where()
                .eq("approved" , true)
                .and()
                .ge("start_date", now)
                .or()
                .ge("end_date", now)
                .query()
        })
    }

    fun getAll(group: Group): CompletableFuture<List<Appointment>> {
        return futureOf(Supplier {
            val classroomJoin = classroomDao.queryBuilder()
            classroomJoin.where().eq("group_id", group.id)
            val query = appointmentDao.queryBuilder().leftJoin(classroomJoin)
            query.query()
        })
    }

    fun getOngoingByClassroom(classroom: Int, group: Group): CompletableFuture<List<Appointment>> {
        return futureOf(Supplier {
            val now = Date()
            val classroomJoin = classroomDao.queryBuilder()
            classroomJoin.where().eq("group_id", group.id)
            val query = appointmentDao.queryBuilder().leftJoin(classroomJoin)
            query.orderBy("start_date", true)
                .where()
                .ge("start_date", now)
                .and()
                .eq("classroom_id", classroom)
                .query()
        })
    }

    fun getOngoingByUser(user: Int, group: Group): CompletableFuture<List<Appointment>> {
        return futureOf(Supplier {
            val now = Date()
            val classroomJoin = classroomDao.queryBuilder()
            classroomJoin.where().eq("group_id", group.id)
            val query = appointmentDao.queryBuilder().leftJoin(classroomJoin)
            query
                .orderBy("start_date", true)
                .where()
                .ge("start_date", now)
                .and()
                .eq("user_id", user)
                .query()
        })
    }
}
