package org.svenson.benchmark;

import org.openjdk.jmh.annotations.*;

public class BenchMark {
    @Benchmark
    public void parser(ExecutionPlan plan) {
        plan.deserializer.read( plan.json, Bean.class);
    }

    @Benchmark
    public void generator(ExecutionPlan plan) {
        plan.serializer.dump(plan.dump);
    }
}
