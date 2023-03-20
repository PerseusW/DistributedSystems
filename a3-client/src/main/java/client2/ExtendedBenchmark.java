package client2;

import client1.Benchmark;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.style.Styler;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Maidi Wang
 * This is the benchmark result for client2, adding stats for latency distribution and plotting throughput according
 * to time.
 */
public class ExtendedBenchmark extends Benchmark {
    private final List<Record> records;

    public ExtendedBenchmark(Benchmark benchmark, List<Record> records) {
        super(benchmark.successes, benchmark.failures, benchmark.wallTime);
        this.records = records;
    }

    /**
     * Appends additional information on top of the basic benchmark results.
     */
    @Override
    public String toString() {
        // Need to sort before getting latency percentiles, so sort by latency.
        records.sort((first, second) -> (int) (first.latency - second.latency));

        // Get mean latency.
        double mean = records.stream().collect(Collectors.averagingLong(record -> record.latency));

        // Get median latency.
        double median = records.get(records.size() / 2).latency;
        if (records.size() % 2 == 0) {
            median += records.get(records.size() / 2 - 1).latency;
            median /= 2;
        }

        // Get 99th percentile latency.
        double percentile = records.get((int) (records.size() * 0.99)).latency;

        // Get min/max latency.
        double min = records.get(0).latency;
        double max = records.get(records.size() - 1).latency;

        // Append this information into previous benchmarks.
        StringBuilder builder = new StringBuilder(super.toString()).append(System.lineSeparator());
        builder.append("Mean latency: ").append(mean).append(" ms").append(System.lineSeparator());
        builder.append("Median latency: ").append(median).append(" ms").append(System.lineSeparator());
        builder.append("99th percentile latency: ").append(percentile).append(" ms").append(System.lineSeparator());
        builder.append("Min latency: ").append(min).append(" ms").append(System.lineSeparator());
        builder.append("Max latency: ").append(max).append(" ms");
        return builder.toString();
    }

    /**
     * Utilizes XChart to plot a bar chart for throughput over time.
     */
    @Override
    public CategoryChart plot() {
        // Need to sort by startTime first.
        records.sort((first, second) -> (int) (first.startTime - second.startTime));
        // startTime is in milliseconds.
        long start = records.get(0).startTime;
        long end = records.get(records.size() - 1).startTime;
        int length = (int) ((end - start) / 1000 + 1);
        int[] xs = new int[length];
        int[] ys = new int[length];

        for (int i = 0; i < length; i++) {
            xs[i] = i;
        }
        for (Record record : records) {
            int index = (int) ((record.startTime - start) / 1000);
            ys[index]++;
        }

        CategoryChart chart = super.plot();
        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNW);
        chart.addSeries("Throughput", xs, ys);
        new SwingWrapper<>(chart).displayChart();
        return chart;
    }

    /**
     * Used for getting latency under load.
     */
    @Override
    public double getLatency() {
        return records.stream().collect(Collectors.averagingLong(record -> record.latency));
    }
}
