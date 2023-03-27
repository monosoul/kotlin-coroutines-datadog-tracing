package dev.monosoul.coroutines.tracing

import datadog.trace.api.Trace
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class SecondLevelComponent2 {
    private val httpCallingComponent = HttpCallingComponent()

    @Trace
    suspend fun annotationTracedCall() {
        withContext(CoroutineName("secondLevelCall2_1")) {
            println("Delay before call")
            Thread.sleep(200)
            delay(100)
        }
        withContext(CoroutineName("secondLevelCall2_2")) {
            httpCallingComponent.annotationTracedHttpCall()
        }
        withContext(CoroutineName("secondLevelCall2_3")) {
            delay(100)
            println("Delay after call")
            Thread.sleep(200)
        }
    }

    suspend fun helperTracedCall() {
        coRunTraced("helperTracedCall") {
            withContext(CoroutineName("secondLevelCall2_1")) {
                println("Delay before call")
                Thread.sleep(200)
                delay(100)
            }
            withContext(CoroutineName("secondLevelCall2_2")) {
                httpCallingComponent.helperTracedHttpCall()
            }
            withContext(CoroutineName("secondLevelCall2_3")) {
                delay(100)
                println("Delay after call")
                Thread.sleep(200)
            }
        }
    }
}
