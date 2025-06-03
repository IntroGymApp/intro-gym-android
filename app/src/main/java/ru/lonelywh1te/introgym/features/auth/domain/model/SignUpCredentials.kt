package ru.lonelywh1te.introgym.features.auth.domain.model

data class SignUpCredentials(
    val username: String,
    val userCredentials: UserCredentials,
)
