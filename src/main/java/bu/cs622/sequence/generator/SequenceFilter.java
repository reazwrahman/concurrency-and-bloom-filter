package bu.cs622.sequence.generator;

import java.util.concurrent.locks.ReentrantReadWriteLock;
import com.google.common.base.Charsets;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

public class SequenceFilter {

    private BloomFilter<String> m_bloomFilter;
    private Runtime runtime = Runtime.getRuntime();
    public long peakMemory = runtime.totalMemory() - runtime.freeMemory();

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
    private final ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();

    public SequenceFilter(int expectedRecords) {
        // Create a Bloom Filter for integers with expected insertions and false positive probability
        m_bloomFilter = BloomFilter.create(Funnels.stringFunnel(Charsets.UTF_8), expectedRecords, 0.01);
    }

    public void insert(String record){
        writeLock.lock();
        try {
            m_bloomFilter.put(record);
            peakMemory = Math.max(runtime.totalMemory() - runtime.freeMemory(), peakMemory);
        }finally {
            writeLock.unlock();
        }
    }

    public boolean checkMembership(String record){
        readLock.lock();
        try {
            return m_bloomFilter.mightContain(record);
        }finally {
            readLock.unlock();
        }
    }

    public long getApprxoimateSize(){
        readLock.lock();
        try {
            return m_bloomFilter.approximateElementCount();
        }finally {
            readLock.unlock();
        }
    }
}
