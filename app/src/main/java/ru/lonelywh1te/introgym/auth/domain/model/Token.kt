package ru.lonelywh1te.introgym.auth.domain.model

data class Token(
    val accessToken: String,
    val refreshToken: String,
)