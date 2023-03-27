package dev.monosoul.coroutines.tracing

import datadog.trace.api.Trace
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.statement.readBytes
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.withContext

private val httpClient = HttpClient(CIO)

class HttpCallingComponent {
    @Trace
    suspend fun annotationTracedHttpCall(): String = withContext(CoroutineName("httpCall")) {
        httpClient.get("https://github.com")
            .readBytes()
            .decodeToString()
    }

    suspend fun helperTracedHttpCall(): String = coRunTraced("helperTracedHttpCall") {
        withContext(CoroutineName("httpCall")) {
            httpClient.get("https://github.com")
                .readBytes()
                .decodeToString()
        }
    }
}
