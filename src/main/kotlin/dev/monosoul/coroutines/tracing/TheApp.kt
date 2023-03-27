package dev.monosoul.coroutines.tracing

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.time.Duration.Companion.seconds

val topLevelComponent = TopLevelComponent()

fun main() {
    runBlocking {
        while (true) {
            topLevelComponent.annotationTracedCall()
            delay(100)
            topLevelComponent.helperTracedCall()
            delay(5.seconds)
        }
    }
}
