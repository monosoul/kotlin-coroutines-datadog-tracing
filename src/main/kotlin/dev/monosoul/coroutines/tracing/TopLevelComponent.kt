package dev.monosoul.coroutines.tracing

import datadog.trace.api.Trace
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class TopLevelComponent {

    private val secondLevelComponent1 = SecondLevelComponent1()
    private val secondLevelComponent2 = SecondLevelComponent2()

    @Trace
    suspend fun annotationTracedCall() {
        withContext(CoroutineName("topLevelCall") + Dispatchers.IO) {
            val asyncCalls = listOf(
                async { secondLevelComponent1.annotationTracedCall() },
                async { secondLevelComponent2.annotationTracedCall() }
            )
            delay(100)
            asyncCalls.awaitAll()
        }
    }

    suspend fun helperTracedCall() {
        coRunTraced("helperTracedCall") {
            withContext(CoroutineName("topLevelCall") + Dispatchers.IO) {
                val asyncCalls = listOf(
                    async { secondLevelComponent1.helperTracedCall() },
                    async { secondLevelComponent2.helperTracedCall() }
                )
                delay(100)
                asyncCalls.awaitAll()
            }
        }
    }
}
