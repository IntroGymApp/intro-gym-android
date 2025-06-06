package ru.lonelywh1te.introgym.features.auth.domain.model

data class TokenPair(
    val accessToken: String,
    val refreshToken: String,
)
