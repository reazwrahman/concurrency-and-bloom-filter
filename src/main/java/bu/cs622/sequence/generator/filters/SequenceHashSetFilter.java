package bu.cs622.sequence.generator.filters;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Name: Reaz W. Rahman
 * Course: CS 622: Advanced Programming Techniques
 * Date: 2/19/2025
 * File name: SequenceHashSetFilter.java
 * Description: This class implements the Filter interface and uses a HashSet to store unique sequences.
 */

public class SequenceHashSetFilter implements Filter {
    private final Set<String> set;
    private final Runtime runtime = Runtime.getRuntime();
    private long peakMemory = runtime.totalMemory() - runtime.freeMemory();

    public SequenceHashSetFilter() {
        this.set = ConcurrentHashMap.newKeySet();
    }

    // Insert a record into the HashSet
    @Override
    public void insert(String record) {
        set.add(record);
        peakMemory = Math.max(runtime.totalMemory() - runtime.freeMemory(), peakMemory);
    }

    // Check if a record is a member of the HashSet
    @Override
    public boolean checkMembership(String record) {
        return set.contains(record);
    }

    // Get the approximate size of the filter
    @Override
    public long getApprxoimateSize() {
        return set.size();
    }

    // Get the peak memory used by the filter
    @Override
    public long getPeakMemory() {
        return peakMemory;
    }
}
