package benchmark

import java.util.concurrent.TimeUnit
import org.openjdk.jmh.annotations.*
import org.openjdk.jmh.runner.Runner
import org.openjdk.jmh.runner.options.OptionsBuilder
import java.util.concurrent.*


@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 0)
@Measurement(iterations = 1, time = 1, timeUnit = TimeUnit.SECONDS)
class KtsTestBenchmark {
    private var data = 0.0

    @Setup
    fun setUp() {
        data = 3.0
    }

    @Benchmark
    fun sqrtBenchmark(): Double {
        return Math.sqrt(data)
    }

    @Benchmark
    fun cosBenchmark(): Double {
        return Math.cos(data)
    }
}


/*
 * ============================== HOW TO RUN THIS TEST: ====================================
 *
 * You are expected to see the run with large number of iterations, and
 * very large throughput numbers. You can see that as the estimate of the
 * harness overheads per method call. In most of our measurements, it is
 * down to several cycles per call.
 *
 * a) Via command-line:
 *    $ mvn clean install
 *    $ java -jar target/benchmarks.jar JMHSample_01
 *
 * JMH generates self-contained JARs, bundling JMH together with it.
 * The runtime options for the JMH are available with "-h":
 *    $ java -jar target/benchmarks.jar -h
 *
 * b) Via the Java API:
 *    (see the JMH homepage for possible caveats when running from IDE:
 *      http://openjdk.java.net/projects/code-tools/jmh/)
 */
//fun main() {
//    val opt = OptionsBuilder()
//        .include(ReadFileTest::class.java.simpleName)
//        .forks(1)
//        .build()
//
//    Runner(opt).run()
//}
//
//class ReadFileTest {
//
//
//    @Benchmark
//    fun wellHelloThere() {
//        // this method was intentionally left blank.
//    }
//
//}
//

