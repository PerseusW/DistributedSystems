package client1;

import common.BenchMarkerBase;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * @author Maidi Wang
 * This is a specialized version of BenchMarkerBase that simply sends 1 POST request with random SwipeDetails,
 * retries 5 times on failure, and logs the outcome "failure/success".
 * It is the bare minimum needed to satisfy Client (part 1) specifications.
 */
public class Benchmarker extends BenchMarkerBase {
    public Benchmarker(int threads) {
        super(threads);
    }

    @Override
    protected Runnable getTestBody() {
        return () -> {
            HttpRequest request = this.getRandomRequest();
            try {
                HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
                // Handle errors: retry 5 times before assuming failure.
                for (int i = 0; response.statusCode() != 201 && i < 5; ++i) {
                    response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
                }
                if (response.statusCode() == 201) {
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
}
