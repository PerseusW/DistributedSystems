package client3;

import com.google.gson.Gson;
import model.SwipeDetails;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ReadWriteBenchmarker {
    // Serialization/deserialization tool.
    private static final Gson GSON = new Gson();

    // Separate thread pools for POST requests and GET requests.
    private static final int POST_THREADS = 900;
    private static final ThreadPoolExecutor postPool = new ThreadPoolExecutor(POST_THREADS, POST_THREADS, 5, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
    private static final int GET_THREADS = 5;
    private static final ScheduledThreadPoolExecutor getPool = new ScheduledThreadPoolExecutor(GET_THREADS);

    // Requests sent.
    private static final int REQUESTS = 500000;

    // Backend service address.
    private static final String URL = "http://50.112.218.162:8080/a3-backend/";

    private static HttpRequest randomSwipePostRequest() {
        return HttpRequest
                .newBuilder(URI.create(URL + "swipe/" + SwipeDetails.getRandomSwipe()))
                .POST(HttpRequest.BodyPublishers.ofString(GSON.toJson(SwipeDetails.getRandomSwipeDetails())))
                .build();
    }

    private static HttpRequest randomMatchesGetRequest() {
        return HttpRequest
                .newBuilder(URI.create(URL + "matches/" + SwipeDetails.getRandomSwiper()))
                .GET()
                .build();
    }

    private static HttpRequest randomStatsGetRequest() {
        return HttpRequest
                .newBuilder(URI.create(URL + "stats/" + SwipeDetails.getRandomSwiper()))
                .GET()
                .build();
    }

    // Http request utils.
    private static final HttpClient CLIENT = HttpClient.newHttpClient();

    private static boolean sendRequest(Callable<HttpRequest> function) {
        try {
            HttpResponse<String> response = CLIENT.send(function.call(), HttpResponse.BodyHandlers.ofString());
            for (int i = 0; response.statusCode() / 100 != 2 && i < 5; i++) {
                response = CLIENT.send(function.call(), HttpResponse.BodyHandlers.ofString());
            }
            return response.statusCode() / 100 == 2;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        // Post outcomes, thread-safe.
        AtomicInteger postSuccesses = new AtomicInteger(0);
        List<Double> postLatencies = Collections.synchronizedList(new ArrayList<>(REQUESTS));

        double wallTime = System.nanoTime();
        // Submit tasks to post requests pool.
        for (int i = 0; i < REQUESTS; i++) {
            postPool.execute(() -> {
                double start = System.nanoTime();
                if (sendRequest(ReadWriteBenchmarker::randomSwipePostRequest)) {
                    postSuccesses.incrementAndGet();
                }
                postLatencies.add((System.nanoTime() - start) / 1e9);
            });
        }

        AtomicInteger getSuccesses = new AtomicInteger(0);
        List<Double> getLatencies = Collections.synchronizedList(new ArrayList<>());
        getPool.scheduleAtFixedRate(() -> {
            double start = System.nanoTime();
            if (ThreadLocalRandom.current().nextBoolean()) {
                if (sendRequest(ReadWriteBenchmarker::randomMatchesGetRequest)) {
                    getSuccesses.incrementAndGet();
                }
            } else {
                if (sendRequest(ReadWriteBenchmarker::randomStatsGetRequest)) {
                    getSuccesses.incrementAndGet();
                }
            }
            getLatencies.add((System.nanoTime() - start) / 1e9);
        }, 0, 200, TimeUnit.MILLISECONDS);

        // Shutdown thread pools.
        postPool.shutdown();
        if (!postPool.awaitTermination(5, TimeUnit.MINUTES)) {
            postPool.shutdownNow();
        }
        getPool.shutdown();
        if (!getPool.awaitTermination(5, TimeUnit.SECONDS)) {
            getPool.shutdownNow();
        }

        wallTime = (System.nanoTime() - wallTime) / 1e9;

        // Print out benchmarks.
        System.out.println("***POST REQUESTS");
        System.out.println("Successful requests: " + postSuccesses);
        System.out.println("Failed requests: " + (REQUESTS - postSuccesses.get()));
        System.out.println("Wall time: " + wallTime + " s");
        System.out.println("Throughput: " + (postSuccesses.get() / wallTime) + " req/s");
        Collections.sort(postLatencies);
        System.out.println("Min latency: " + (postLatencies.get(0) * 1e3) + " ms");
        System.out.println("Mean latency: " + (postLatencies.stream().collect(Collectors.averagingDouble(latency -> latency)) * 1e3) + " ms");
        System.out.println("Max latency: " + (postLatencies.get(postLatencies.size() - 1) * 1e3) + " ms");
        System.out.println("Mid latency: " + (postLatencies.get(postLatencies.size() / 2) * 1e3) + " ms");
        System.out.println("99th percentile latency: " + (postLatencies.get(postLatencies.size() * 99 / 100) * 1e3) + " ms");
        System.out.println();

        System.out.println("***GET REQUESTS");
        System.out.println("Successful requests: " + getSuccesses);
        Collections.sort(getLatencies);
        System.out.println("Min latency: " + (getLatencies.get(0) * 1e3) + " ms");
        System.out.println("Mean latency: " + (getLatencies.stream().collect(Collectors.averagingDouble(latency -> latency)) * 1e3) + " ms");
        System.out.println("Max latency: " + (getLatencies.get(getLatencies.size() - 1) * 1e3) + " ms");
    }
}
