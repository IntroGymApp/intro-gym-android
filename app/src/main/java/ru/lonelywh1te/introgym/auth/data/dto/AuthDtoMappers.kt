package ru.lonelywh1te.introgym.auth.data.dto

import ru.lonelywh1te.introgym.auth.data.dto.otp_confirm.ConfirmOtpResponseDto
import ru.lonelywh1te.introgym.auth.data.dto.otp_send.SendOtpResponseDto
import ru.lonelywh1te.introgym.auth.data.dto.refresh_tokens.RefreshTokensResponseDto
import ru.lonelywh1te.introgym.auth.data.dto.sign_in.SignInResponseDto
import ru.lonelywh1te.introgym.auth.data.dto.sign_up.SignUpResponseDto
import ru.lonelywh1te.introgym.auth.domain.model.ConfirmOtpResult
import ru.lonelywh1te.introgym.auth.domain.model.SendOtpResult
import ru.lonelywh1te.introgym.auth.domain.model.Token

fun SendOtpResponseDto.toSendOtpResult(): SendOtpResult {
    return SendOtpResult(sessionId = this.sessionId)
}

fun ConfirmOtpResponseDto.toConfirmOtpResult(): ConfirmOtpResult {
    return ConfirmOtpResult(isSuccess = this.isSuccess)
}

fun SignUpResponseDto.toToken(): Token {
    return Token(accessToken = this.accessToken, refreshToken = this.refreshToken)
}

fun SignInResponseDto.toToken(): Token {
    return Token(accessToken = this.accessToken, refreshToken = this.refreshToken)
}

fun RefreshTokensResponseDto.toToken(): Token {
    return Token(accessToken = this.accessToken, refreshToken = this.refreshToken)
}

