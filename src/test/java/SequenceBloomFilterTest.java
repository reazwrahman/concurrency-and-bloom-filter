import bu.cs622.sequence.generator.filters.SequenceBloomFilter;
import org.junit.Test;

import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

public class SequenceBloomFilterTest {

    @Test
    public void testBloomFilter(){
        SequenceBloomFilter filter = new SequenceBloomFilter(10);
        filter.insert("apple");
        filter.insert("orange");
        filter.insert("banana");

        assertTrue(filter.checkMembership("apple"));
        assertTrue(filter.checkMembership("banana"));
        assertFalse(filter.checkMembership("grapes"));

        assertEquals(filter.getApprxoimateSize(), 3);

    }

    @Test
    public void testBloomFilterConcurrently() throws InterruptedException {
        SequenceBloomFilter filter = new SequenceBloomFilter(10);
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(() -> {
            filter.insert("apple");
            filter.insert("orange");
            filter.insert("banana");
        }); // task1
        executor.submit(() -> {
            filter.insert("mango");
            filter.insert("guava");
            filter.insert("banana"); // duplicate
        }); // task2

        shutdownExecutor(executor);

        assertTrue(filter.checkMembership("apple"));
        assertTrue(filter.checkMembership("mango"));
        assertFalse(filter.checkMembership("grapes"));

        assertEquals(filter.getApprxoimateSize(), 5);

    }

    // helper method to shut down an executor service
    private void shutdownExecutor(ExecutorService executor) throws InterruptedException, RuntimeException {
        executor.shutdown();
        while (!executor.isTerminated()) {
            executor.awaitTermination(1, TimeUnit.MILLISECONDS);
        }
        if(!executor.isShutdown()) {
            throw new RuntimeException("ThreadHelper::shutdownExecutor failed to shut down executor service");
        }
    }
}
