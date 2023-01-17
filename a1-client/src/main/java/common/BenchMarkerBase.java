package common;

import client1.Benchmark;
import com.google.gson.Gson;
import model.SwipeDetails;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Maidi Wang
 * Base class that takes care of executions:
 * 1. Server address and path to test.
 * 2. Generating random data payload/body.
 * 3. Creating thread pool and executing.
 * 4. Recording basic pass/fail result count in thread safe manner.
 */
public abstract class BenchMarkerBase {
    protected final HttpClient CLIENT = HttpClient.newHttpClient();
    protected final Gson GSON = new Gson();
    protected final String IP = "0.0.0.0";
    protected final String PORT = "8080";
    protected final String CONTEXT = "a1-servlet";
    protected final String PATH = "swipe";
    protected final String URL = String.format("http://%s:%s/%s/%s/", IP, PORT, CONTEXT, PATH);
    protected final AtomicInteger successes = new AtomicInteger(0);
    protected final AtomicInteger failures = new AtomicInteger(0);
    // Allow for polymorphism.
    protected abstract Runnable getTestBody();
    protected final Runnable runnable = getTestBody();
    protected final int threads;

    protected BenchMarkerBase(int threads) {
        this.threads = threads;
    }

    /**
     * Creates thread pool of size `threads` and sends `requests` amount of requests.
     */
    public Benchmark run(int requests) throws InterruptedException {
        long start = System.currentTimeMillis();
        successes.set(0);
        failures.set(0);

        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(threads, threads, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        for (int i = 0; i < requests; ++i) {
            threadPool.execute(runnable);
        }
        // Stop receiving new tasks.
        threadPool.shutdown();
        // Wait 5 minutes for tasks to finish, then close neatly.
        if (!threadPool.awaitTermination(5, TimeUnit.MINUTES)) {
            threadPool.shutdownNow();
        }
        // Time is retrieved in milliseconds.
        double wallTime = (System.currentTimeMillis() - start) / 1000f;

        return new Benchmark(successes.get(), failures.get(), wallTime);
    }

    /**
     * Generates random POST request with valid body.
     */
    protected HttpRequest getRandomRequest() {
        return HttpRequest
                .newBuilder(URI.create(URL.concat(SwipeDetails.getRandomSwipe())))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(GSON.toJson(SwipeDetails.getRandomSwipeDetails()))).build();
    }
}
