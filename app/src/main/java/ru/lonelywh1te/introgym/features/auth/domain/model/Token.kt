package ru.lonelywh1te.introgym.features.auth.domain.model

data class Token(
    val accessToken: String,
    val refreshToken: String,
)