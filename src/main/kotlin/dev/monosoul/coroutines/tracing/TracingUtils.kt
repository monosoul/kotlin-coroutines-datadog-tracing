package dev.monosoul.coroutines.tracing

import datadog.trace.api.DDTags
import io.opentracing.Span
import io.opentracing.Tracer
import io.opentracing.log.Fields
import io.opentracing.tag.Tags
import io.opentracing.util.GlobalTracer

suspend fun <T : Any, O> T.coRunTraced(
    methodName: String,
    block: suspend T.(Span) -> O,
): O = coRunTraced(resource = "${javaClass.simpleName}.$methodName", block = block)

fun <T : Any, O> T.runTraced(
    methodName: String,
    block: T.(Span) -> O,
): O = runTraced(resource = "${javaClass.simpleName}.$methodName", block = block)

suspend fun <T, O> T.coRunTraced(
    resource: String,
    operation: String = "service.call",
    block: suspend T.(Span) -> O,
): O = inlineRunTraced(operation, resource) { block(it) }

fun <T, O> T.runTraced(
    resource: String,
    operation: String = "service.call",
    block: T.(Span) -> O,
): O = inlineRunTraced(operation, resource) { block(it) }

private inline fun <T, O> T.inlineRunTraced(
    operation: String,
    resource: String,
    block: T.(Span) -> O,
): O {
    val tracer = GlobalTracer.get()
    val span = tracer.startSpan(operation, resource)

    return runCatching {
        tracer.activateSpan(span).use {
            block(span)
        }
    }.onFailure {
        span.finish(it)
    }.onSuccess {
        span.finish()
    }.getOrThrow()
}

private fun Tracer.startSpan(operation: String, resource: String): Span =
    buildSpan(operation).withTag(DDTags.RESOURCE_NAME, resource).start()

private fun Span.finish(e: Throwable) {
    setTag(Tags.ERROR, true)
    log(mapOf(Fields.ERROR_OBJECT to e))
    finish()
}
