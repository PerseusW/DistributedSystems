package client1;

import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;

/**
 * @author Maidi Wang
 * This is the basic result that benchmarkers will yield after sending requests and receiving responses.
 * Fields are final, so it is safe to expose them as public for accessing.
 * For most of the time, these results only need to be printed.
 */
public class Benchmark {
    public final int successes;
    public final int failures;
    public final double wallTime;
    public final double throughput;

    /**
     * Throughput is automatically calculated with successes and wallTime.
     */
    public Benchmark(int successes, int failures, double wallTime) {
        this.successes = successes;
        this.failures = failures;
        this.wallTime = wallTime;
        this.throughput = successes / wallTime;
    }

    /**
     * Serializing results into print-friendly format, along with units.
     */
    @Override
    public String toString() {
        return "***Benchmarks" + System.lineSeparator()
                + "Successful requests: " + successes + System.lineSeparator()
                + "Failed requests: " + failures + System.lineSeparator()
                + "Total run time: " + wallTime + " s" + System.lineSeparator()
                + "Throughput: " + throughput + " req/s";
    }

    /**
     * Creating an empty chart to be used for extended benchmark results.
     */
    public CategoryChart plot() {
        return new CategoryChartBuilder()
                .width(800)
                .height(800)
                .title("Throughput Over Time")
                .xAxisTitle("Time")
                .yAxisTitle("Throughput")
                .build();
    }

    public double getLatency() {
        throw new RuntimeException("Basic benchmark doesn't record latency");
    }
}
