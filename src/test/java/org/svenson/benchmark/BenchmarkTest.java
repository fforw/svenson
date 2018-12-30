package org.svenson.benchmark;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.infra.BenchmarkParams;
import org.openjdk.jmh.results.BenchmarkResult;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;
import org.openjdk.jmh.runner.options.VerboseMode;
import org.svenson.benchmark.ExecutionPlan.Framework;
import org.svenson.benchmark.ExecutionPlan.Scenario;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import static org.svenson.benchmark.ExecutionPlan.Framework.SVENSON;
import static org.svenson.benchmark.ExecutionPlan.Scenario.PLAIN;

@Category(BenchMark.class)
public class BenchmarkTest {

    /**
     * Benchmark run with Junit
     *
     * @throws Exception
     */
    @Test
    public void runTest() throws Exception {
        Options opt = initBench();
        Collection<RunResult> results = runBench(opt);
        assertOutputs(results);
        saveOutputs(results);
    }

    private void saveOutputs(Collection<RunResult> results) throws IOException {
        Table table = new Table();
        for (RunResult result : results) {
            for (BenchmarkResult benchmarkResult : result.getBenchmarkResults()) {
                table.add("Benchmark", benchmarkResult.getParams().getBenchmark());
                for (String key : benchmarkResult.getParams().getParamsKeys()) {
                    table.add(key, benchmarkResult.getParams().getParam(key));
                }
                table.add("Score", benchmarkResult.getPrimaryResult().getScore());
                table.add("Unit", benchmarkResult.getPrimaryResult().getScoreUnit());
            }
        }
        table.writeTo(new OutputStreamWriter(System.out));

        try (final BufferedWriter writer = Files.newBufferedWriter(Paths.get("target/benchmark.md"), Charset.defaultCharset())) {
            table.writeTo(writer);
        }

    }

    /**
     * Runner options that runs all benchmarks in this test class
     * namely benchmark oldWay and newWay.
     *
     * @return
     */
    private Options initBench() {
        return new OptionsBuilder() //
                .include(BenchMark.class.getSimpleName() + ".*") //
                .mode(Mode.Throughput) //
                .verbosity(VerboseMode.NORMAL) //
                .timeUnit(TimeUnit.MILLISECONDS) //
                .warmupTime(TimeValue.seconds(1)) //
                .measurementTime(TimeValue.milliseconds(1000)) //
                .measurementIterations(4) //
                .threads(4)
                .syncIterations(true)
                .warmupIterations(4) //
                .shouldFailOnError(false) //
                .shouldDoGC(true) //
                .forks(1) //
                .build();
    }

    /**
     * @param opt
     * @return
     * @throws RunnerException
     */
    private Collection<RunResult> runBench(Options opt) throws RunnerException {
        return new Runner(opt).run();
    }

    /**
     * Assert benchmark results that are interesting for us
     * Asserting test mode and average test time
     *
     * @param results
     */
    private void assertOutputs(Collection<RunResult> results) {
        for (RunResult r : results) {
            for (BenchmarkResult rr : r.getBenchmarkResults()) {
                final BenchmarkParams params = rr.getParams();
                Mode mode = params.getMode();
                final Framework framework = Framework.valueOf(params.getParam("framework"));
                final int items = Integer.parseInt(params.getParam("items"));
                final int nested = Integer.parseInt(params.getParam("nested"));
                final Scenario scenario = Scenario.valueOf(params.getParam("scenario"));
                if (framework == SVENSON && items == 1 && nested == 1 && scenario == PLAIN) {
                    double score = rr.getPrimaryResult().getScore();
                    String methodName = rr.getPrimaryResult().getLabel();
                    Assert.assertEquals("Test mode is not average mode. Method = " + methodName,
                            Mode.Throughput, mode);
                    Assert.assertTrue("Benchmark score = " + score + " is higher than " + 1000_000_000 + " " + rr.getScoreUnit() + ". Too slow performance !",
                            score < 1000_000_000);
                }
            }
        }
    }
}
