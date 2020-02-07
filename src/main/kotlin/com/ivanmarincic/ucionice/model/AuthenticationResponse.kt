package com.ivanmarincic.ucionice.model

import java.util.*

data class AuthenticationResponse(
    val token: String,
    val expiresAt: Date,
    val user: User
)
