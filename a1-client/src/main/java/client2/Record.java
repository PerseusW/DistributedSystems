package client2;

/**
 * @author Maidi Wang
 * Fields are final, so it is safe to expose them as public.
 */
public class Record {
    public final long startTime;
    public final String requestType;
    public final long latency;
    public final int responseCode;

    public Record(long startTime, String requestType, long latency, int responseCode) {
        this.startTime = startTime;
        this.requestType = requestType;
        this.latency = latency;
        this.responseCode = responseCode;
    }
}
