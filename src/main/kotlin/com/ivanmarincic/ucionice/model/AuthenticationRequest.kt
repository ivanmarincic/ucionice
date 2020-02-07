package com.ivanmarincic.ucionice.model

data class AuthenticationRequest(
    val password: String,
    val email: String
)
