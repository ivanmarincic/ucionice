package com.ivanmarincic.ucionice.dao

import com.ivanmarincic.ucionice.model.User
import com.j256.ormlite.dao.BaseDaoImpl
import com.j256.ormlite.support.ConnectionSource

class UsersDao(connectionSource: ConnectionSource) : BaseDaoImpl<User, Int>(connectionSource, User::class.java) {

    fun getByEmail(email: String): User? {
        return queryBuilder()
            .where()
            .eq("email", email)
            .queryForFirst()
    }
}
