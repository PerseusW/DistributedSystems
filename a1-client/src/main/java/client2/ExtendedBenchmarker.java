package client2;

import client1.Benchmark;
import common.BenchMarkerBase;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Maidi Wang
 * This is the extended version of Benchmarker that records more verbose information about request time, type, latency
 * and response code.
 */
public class ExtendedBenchmarker extends BenchMarkerBase {
    private final List<Record> records = Collections.synchronizedList(new ArrayList<>(500000));

    public ExtendedBenchmarker(int threads) {
        super(threads);
    }

    /**
     * Test body records request startTime, type, latency, response code and saves it in an in-memory thread-safe list.
     */
    @Override
    protected Runnable getTestBody() {
        return () -> {
            HttpRequest request = this.getRandomRequest();
            try {
                long start = System.currentTimeMillis();
                HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
                // Handle errors: retry 5 times before assuming failure.
                for (int i = 0; response.statusCode() / 100 != 2 && i < 5; ++i) {
                    response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
                }
                long latency = System.currentTimeMillis() - start;
                records.add(new Record(start, "POST", latency, response.statusCode()));
                if (response.statusCode() / 100 == 2) {
                    int count = successes.incrementAndGet();
                    // Print debug messages to make sure client is still alive.
                    if (count % 1000 == 0) {
                        System.out.println("Progress: " + count + " successes");
                    }
                } else {
                    failures.incrementAndGet();
                }
            } catch (Exception e) {
                e.printStackTrace();
                failures.incrementAndGet();
            }
        };
    }

    /**
     * Utilizes polymorphism to display extended benchmark results.
     */
    @Override
    public Benchmark run(int requests) throws InterruptedException {
        return new ExtendedBenchmark(super.run(requests), this.records);
    }
}
