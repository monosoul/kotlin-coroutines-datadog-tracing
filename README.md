# kotlin-coroutines-datadog-tracing
Repo to de
monstrate the difference between tracing approaches

To run DD agent container:
```shell
docker run -it --cgroupns host \
                --pid host \
                -v /var/run/docker.sock:/var/run/docker.sock:ro \
                -v /proc/:/host/proc/:ro \
                -v /sys/fs/cgroup/:/host/sys/fs/cgroup:ro \
                -p 127.0.0.1:8126:8126/tcp \
                -e DD_DOGSTATSD_NON_LOCAL_TRAFFIC=true \
                -e DD_APM_ENABLED=true \
                -e DD_APM_NON_LOCAL_TRAFFIC=true \
                -e DD_API_KEY=$DD_API_KEY \
                -e DD_SITE="datadoghq.eu" \
                gcr.io/datadoghq/agent:latest
```

To run the app with DD agent and coroutines instrumentation enabled:
```shell
./gradlew runTheApp
```

### The app will repeatedly generate 2 traces:
1. One using the methods annotated with `@Trace` annotation
2. One using the methods instrumented imperatively with helper functions 
(see kotlin/dev/monosoul/coroutines/tracing/TracingUtils.kt)

### In the first case the trace will look like this:
![with_annotation.png](assets%2Fwith_annotation.png)

### In the second case the trace will look like this:
![with_helpers.png](assets%2Fwith_helpers.png)

As you can see there are no breaks in the traces in the second case,
also the span hierarchy looks better there. 
