package client1;

import client2.ExtendedBenchmarker;

/**
 * @author Maidi Wang
 * This is the main function used to send 500k random POST requests as fast as possible.
 */
public class StatsMain {
    /**
     * TL;DR, Conclusions:
     * 1. Concurrency/Throughput bottleneck is always at client side.
     * 2. Concurrency < number of threads due to synchronization and needs amending before calculations.
     * 3. Server latency can change under load and thus change client latency.
     * <p>
     * By default, Tomcat can allocate at most 200 threads for processing requests.
     * According to my local(no network latency) test results, a request takes 1-2 ms to process using bare servlet.
     * So the server side theoretical throughput under ideal conditions is 100k-200k req/s, with ideal conditions
     * meaning taking no account for concurrency synchronization overhead like thread pool blocking queue.
     * <p>
     * When we have network latency between client and server, like in my case with Seattle and us-west-2 latency being
     * around 65 ms, the concurrency/throughput bottleneck is always on the client side unless the client has threads
     * up to (65 ms / 2 ms) * 200 threads = 6500 threads.
     * Think about it like this, to reach an optimal throughput configuration, this equation should hold true:
     * client send throughput = server process throughput
     * Using Little's Law, Concurrency = Throughput * Latency
     * 1. Throughput is the same for both client and server.
     * 2. Latency is different for client and server(super super important):
     * 2.1. Server latency = server processing time.
     * 2.2. Client latency = network latency(to) + server processing time + network latency(from).
     * 3. Thus, Concurrency configuration is different for server and client:
     * (Client Concurrency / Server Concurrency) = (Client Latency / Server Latency)
     * <p>
     * Since having a thread pool with 6500 threads takes up 6500 threads * 2 MB/thread = 13 GB memory, it is not
     * feasible to do so. Conclusion, the throughput/concurrency bottleneck is always at client side.
     * <p>
     * Even so, we are not done. Under ideal conditions, Concurrency = number of threads. However, conditions are not
     * ideal in that threads have synchronization overhead where a thread might have to wait for:
     * 1. Retrieving a task from thread pool queue.
     * 2. Incrementing AtomicInteger `successes` and `failures`.
     * 3. Writing records to a SynchronizedList.
     * So actually, Concurrency < number of threads.
     * <p>
     * Still one more thing, Server latency can change under load, usually the higher the load, the higher the latency.
     * So we need to calculate the average latency under the same load to achieve accurate results.
     */
    private static final int REQUESTS = 500000;
    private static final int THREADS = 900;

    public static void main(String[] args) throws InterruptedException {
        /**
         * Client latency is approximately 100 ms under load, meaning 10 requests/sec.
         * So to finish in reasonable time, say 60 secs, the number of requests should be around THREADS * 60 * 10.
         * This is the client side latency with components as follows:
         *     Client latency = network latency(to) + server processing time(load) + network latency(from).
         * Unit is milliseconds.
         */
        double latencyMs = new ExtendedBenchmarker(THREADS).run(THREADS * 60 * 10).getLatency();

        System.out.println("***Waiting 10 seconds for things to clear up.");
        // Sleep 10 seconds for things to clear up on both client/server sides
        Thread.sleep(10000, 0);

        // Run and print benchmark.
        Benchmark benchmark = new Benchmarker(THREADS).run(REQUESTS);
        System.out.println(benchmark);

        // Validation.
        System.out.println("***Validation");
        double latency = latencyMs / 1000;
        double theoryThroughput = THREADS / latency;
        double actualThroughput = benchmark.throughput;
        double error = Math.abs(1 - actualThroughput / theoryThroughput) * 100;
        System.out.println("Actual latency under load: " + latencyMs + " ms");
        System.out.println("Theoretical throughput(Concurrency = num of threads): " + theoryThroughput + " req/s");
        System.out.println("Actual throughput: " + actualThroughput + " req/s");
        System.out.println("Throughput error rate: " + error + "%");
    }
}