package dev.monosoul.coroutines.tracing

import datadog.trace.api.Trace
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@Suppress("BlockingMethodInNonBlockingContext")
class SecondLevelComponent1 {
    @Trace
    suspend fun annotationTracedCall() {
        withContext(CoroutineName("secondLevelCall1")) {
            println("Delay before call")
            Thread.sleep(200)
            delay(100)
            println("Do stuff")
            Thread.sleep(200)
            delay(100)
            Thread.sleep(200)
            println("Delay after call")
        }
    }

    suspend fun helperTracedCall() {
        coRunTraced("helperTracedCall") {
            withContext(CoroutineName("secondLevelCall1")) {
                println("Delay before call")
                Thread.sleep(200)
                delay(100)
                println("Do stuff")
                Thread.sleep(200)
                delay(100)
                println("Delay after call")
                Thread.sleep(200)
            }
        }
    }
}
