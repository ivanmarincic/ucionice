package com.ivanmarincic.ucionice.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import com.ivanmarincic.ucionice.Application
import com.ivanmarincic.ucionice.dao.GroupUserDao
import com.ivanmarincic.ucionice.dao.UsersDao
import com.ivanmarincic.ucionice.model.*
import com.ivanmarincic.ucionice.util.exceptions.AuthenticationFailedException
import com.ivanmarincic.ucionice.util.exceptions.AuthorizationFailedException
import com.ivanmarincic.ucionice.util.exceptions.UserExistsException
import com.ivanmarincic.ucionice.util.hashPassword
import com.ivanmarincic.ucionice.util.validatePassword
import com.j256.ormlite.dao.DaoManager
import org.sqlite.SQLiteException
import java.sql.SQLException
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

class UsersService {
    private val usersDao: UsersDao = DaoManager.createDao(Application.connectionSource, User::class.java)
    private val groupUserDao: GroupUserDao = DaoManager.createDao(Application.connectionSource, GroupUser::class.java)
    private val algorithm = Algorithm.HMAC256("Ucionice123")
    private val issuer = "Ucionice"

    private fun createToken(user: User, expiresAt: Date): String {
        return JWT.create()
            .withExpiresAt(expiresAt)
            .withIssuedAt(Date())
            .withClaim("user-id", user.id)
            .withIssuer(issuer)
            .sign(algorithm)
    }


    private fun vertifyToken(token: String): DecodedJWT {
        return JWT
            .require(algorithm)
            .withIssuer(issuer)
            .build()
            .verify(token)
    }

    fun login(authenticationRequest: AuthenticationRequest): AuthenticationResponse {
        val user = usersDao.getByEmail(authenticationRequest.email)
        user?.let {
            if (!validatePassword(authenticationRequest.password, user.password)) {
                throw AuthenticationFailedException()
            }
            val expiration = Date.from(Instant.now().plus(1, ChronoUnit.DAYS))
            return AuthenticationResponse(
                createToken(user, expiration),
                expiration,
                it
            )
        } ?: throw AuthenticationFailedException()
    }

    fun register(user: User): User {
        user.password = hashPassword(user.password)
        try {
            usersDao.create(user)
        } catch (e: SQLException) {
            (e.cause as? SQLiteException)?.let {
                if (it.resultCode.code == 19) throw UserExistsException(user.email)
            } ?: throw e
        }
        return user
    }

    fun authorize(token: String): User {
        try {
            val userId = vertifyToken(token).getClaim("user-id").asInt()
            return usersDao.queryForId(userId) ?: throw AuthorizationFailedException()
        } catch (e: Exception) {
            throw AuthorizationFailedException()
        }
    }

    fun findUsers(query: String, group: Group): List<User> {
        val usersQuery = usersDao.queryBuilder()
        usersQuery.where().like("name", "%$query%")
        return usersQuery.distinct().query()
    }
}
