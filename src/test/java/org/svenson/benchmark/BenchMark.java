package org.svenson.benchmark;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

public class BenchMark {
    @Fork(value = 1, warmups = 0)
    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @Timeout(time = 30, timeUnit = TimeUnit.SECONDS)
    @Warmup(iterations = 1)
    @Measurement(iterations = 2, time = 5)
    public void parser(ExecutionPlan plan) {
        plan.parser.parse(ExecutionPlan.Bean.class, plan.json);
    }

    @Fork(value = 1, warmups = 0)
    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @Timeout(time = 30, timeUnit = TimeUnit.SECONDS)
    @Warmup(iterations = 1)
    @Measurement(iterations = 2, time = 5)
    public void generator(ExecutionPlan plan) {
        plan.generator.forValue(plan.dump);
    }
}
