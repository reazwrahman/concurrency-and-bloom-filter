package bu.cs622.sequence.generator;

import com.google.common.base.Charsets;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SequenceFilter {

    private final BloomFilter<String> m_bloomFilter;
    private final Runtime runtime = Runtime.getRuntime();
    public long peakMemory = runtime.totalMemory() - runtime.freeMemory();

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
    private final ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();

    public SequenceFilter(int expectedRecords) {
        // Create a Bloom Filter for integers with expected insertions and false positive probability
        m_bloomFilter = BloomFilter.create(Funnels.stringFunnel(Charsets.UTF_8), expectedRecords, 0.01);
    }

    // example of write lock
    public void insert(String record){
        writeLock.lock();
        try {
            m_bloomFilter.put(record);
            peakMemory = Math.max(runtime.totalMemory() - runtime.freeMemory(), peakMemory);
        }finally {
            writeLock.unlock();
        }
    }

    // example of read lock
    public boolean checkMembership(String record){
        readLock.lock();
        try {
            return m_bloomFilter.mightContain(record);
        }finally {
            readLock.unlock();
        }
    }

    // example of structured lock
    public synchronized long getApprxoimateSize(){
        return m_bloomFilter.approximateElementCount();
    }
}
