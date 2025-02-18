package bu.cs622.sequence.generator.filters;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class SequenceHashSetFilter implements Filter {
    private final Set<String> set;
    private final Runtime runtime = Runtime.getRuntime();
    private long peakMemory = runtime.totalMemory() - runtime.freeMemory();

    public SequenceHashSetFilter() {
        this.set = ConcurrentHashMap.newKeySet();
    }

    @Override
    public void insert(String record) {
        set.add(record);
        peakMemory = Math.max(runtime.totalMemory() - runtime.freeMemory(), peakMemory);
    }

    @Override
    public boolean checkMembership(String record) {
        return set.contains(record);
    }

    @Override
    public long getApprxoimateSize() {
        return set.size();
    }

    @Override
    public long getPeakMemory() {
        return peakMemory;
    }
}
