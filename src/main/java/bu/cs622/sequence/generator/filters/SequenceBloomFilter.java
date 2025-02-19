package bu.cs622.sequence.generator.filters;

import com.google.common.base.Charsets;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Name: Reaz W. Rahman
 * Course: CS 622: Advanced Programming Techniques
 * Date: 2/19/2025
 * File name: SequenceBloomFilter.java
 * Description: This class implements the Filter interface and uses a Bloom Filter to store and check for unique sequences.
 */

public class SequenceBloomFilter implements Filter {

    private final BloomFilter<String> m_bloomFilter;
    private final Runtime runtime = Runtime.getRuntime();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
    private final ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();
    private long peakMemory = runtime.totalMemory() - runtime.freeMemory();

    // Constructor for the SequenceBloomFilter class
    public SequenceBloomFilter(int expectedRecords) {
        // Create a Bloom Filter for integers with expected insertions and false positive probability
        m_bloomFilter = BloomFilter.create(Funnels.stringFunnel(Charsets.UTF_8), expectedRecords, 0.01);
    }

    // Insert a record into the Bloom Filter
    @Override
    public void insert(String record) {
        writeLock.lock(); // example of write lock
        try {
            m_bloomFilter.put(record);
            peakMemory = Math.max(runtime.totalMemory() - runtime.freeMemory(), peakMemory);
        } finally {
            writeLock.unlock();
        }
    }

    // Check if a record is a member of the Bloom Filter
    @Override
    public boolean checkMembership(String record) {
        readLock.lock(); // example of read lock
        try {
            return m_bloomFilter.mightContain(record);
        } finally {
            readLock.unlock();
        }
    }

    // Get the approximate size of the filter  (example of structured lock)
    @Override
    public synchronized long getApprxoimateSize() {
        return m_bloomFilter.approximateElementCount();
    }

    // Get the peak memory used by the filter
    @Override
    public synchronized long getPeakMemory() {
        return peakMemory;
    }
}
