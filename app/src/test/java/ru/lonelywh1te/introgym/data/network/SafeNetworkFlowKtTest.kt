package ru.lonelywh1te.introgym.data.network

import android.util.Log
import io.mockk.every
import io.mockk.mockkStatic
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import ru.lonelywh1te.introgym.core.result.AppError
import ru.lonelywh1te.introgym.core.result.Result
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class SafeNetworkFlowKtTest {

    @BeforeEach
    fun setUp() {
        mockkStatic(Log::class)
        every { Log.e(any(), any()) } returns 0
        every { Log.w(any(), any<String>()) } returns 0
        every { Log.i(any(), any<String>()) } returns 0
    }

    @Test
    fun `asSafeNetworkFlow emits same values on success`() = runTest {
        val flow = flow {
            emit(Result.Success(Unit))
        }.asSafeNetworkFlow()

        val expected = listOf(Result.Success(Unit))
        val actual = flow.toList()

        assertEquals(expected, actual)
    }

    @Test
    fun `asSafeNetworkFlow catches UnknownHostException and emits NO_INTERNET error`() = runTest {
        val flow = flow<Result<Unit>> {
            throw UnknownHostException()
        }.asSafeNetworkFlow()

        val expected = listOf(Result.Failure(NetworkError.NO_INTERNET))
        val actual = flow.toList()

        assertEquals(expected, actual)
    }

    @Test
    fun `asSafeNetworkFlow catches SocketTimeoutException and emits REQUEST_TIMEOUT error`() = runTest {
        val flow = flow<Result<Unit>> {
            throw SocketTimeoutException()
        }.asSafeNetworkFlow()

        val expected = listOf(Result.Failure(NetworkError.REQUEST_TIMEOUT))
        val actual = flow.toList()

        assertEquals(expected, actual)
    }

    @Test
    fun `asSafeNetworkFlow catches unknown exception and emits UNKNOWN error`() = runTest {
        val flow = flow<Result<Unit>> {
            throw Exception()
        }.asSafeNetworkFlow()

        val expected = listOf(Result.Failure(AppError.UNKNOWN))
        val actual = flow.toList()

        assertEquals(expected, actual)
    }

    @Test
    fun `asSafeNetworkFlow correctly propagates cancellation`() = runTest {
        val flow = flow<Result<Unit>> {
            throw CancellationException()
        }.asSafeNetworkFlow()

        assertThrows<CancellationException> {
            flow.collect { }
        }
    }
}