package fileindexer;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Statistics {
    public final AtomicInteger totalFound = new AtomicInteger(0);
    public final AtomicInteger totalIndexed = new AtomicInteger(0);
    public final Map<String, AtomicInteger> skipped = new ConcurrentHashMap<>();

    public void skip(String reason) {
        skipped.computeIfAbsent(reason, k -> new AtomicInteger(0)).incrementAndGet();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Found: ").append(totalFound.get())
          .append(", Indexed: ").append(totalIndexed.get())
          .append(", Skipped: ").append(skipped);
        return sb.toString();
    }
}
