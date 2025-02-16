package bu.cs622.sequence.generator;

import com.google.common.base.Charsets;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

public class SequenceFilter {

    private BloomFilter<String> m_bloomFilter;
    private Runtime runtime = Runtime.getRuntime();
    public long peakMemory = runtime.totalMemory() - runtime.freeMemory();

    public SequenceFilter(int expectedRecords) {
        // Create a Bloom Filter for integers with expected insertions and false positive probability
        m_bloomFilter = BloomFilter.create(Funnels.stringFunnel(Charsets.UTF_8), expectedRecords, 0.01);
    }

    public synchronized void insert(String record){
        m_bloomFilter.put(record);
        peakMemory = Math.max(runtime.totalMemory() - runtime.freeMemory(), peakMemory);
    }

    public synchronized boolean checkMembership(String record){
        return m_bloomFilter.mightContain(record);
    }

    public synchronized long getApprxoimateSize(){
        return m_bloomFilter.approximateElementCount();
    }
}
