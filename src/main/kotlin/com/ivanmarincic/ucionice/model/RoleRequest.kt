package com.ivanmarincic.ucionice.model

import com.ivanmarincic.ucionice.UserRole

data class RoleRequest(
    val role: UserRole,
    val user: User
)
