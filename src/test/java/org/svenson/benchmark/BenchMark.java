package org.svenson.benchmark;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

public class BenchMark {
    @Fork(value = 1, warmups = 1)
    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @Timeout(time = 30, timeUnit = TimeUnit.SECONDS)
    @Warmup(iterations = 5)
    @Measurement(iterations = 5, time = 15)
    public void parser(ExecutionPlan plan) {
        plan.deserializer.read( plan.json,ExecutionPlan.Bean.class);
    }

    @Fork(value = 1, warmups = 1)
    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @Timeout(time = 30, timeUnit = TimeUnit.SECONDS)
    @Warmup(iterations = 5)
    @Measurement(iterations = 5, time = 15)
    public void generator(ExecutionPlan plan) {
        plan.serializer.dump(plan.dump);
    }
}
