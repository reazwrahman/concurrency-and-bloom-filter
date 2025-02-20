import bu.cs622.sequence.generator.filters.SequenceBloomFilter;
import com.google.common.hash.Funnel;
import com.google.common.hash.Funnels;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;


public class SequenceBloomFilterTest {
    static SequenceBloomFilter filter;

    @BeforeAll
    public static void setUp() {
        Funnel<CharSequence> funnel = Funnels.stringFunnel(java.nio.charset.StandardCharsets.UTF_8);
        filter = new SequenceBloomFilter<>(10, funnel);
    }

    @Test
    public void testBloomFilter(){
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
        assertDoesNotThrow(filter::getPeakMemory);

    }

    @Test
    public void testBloomFilterWithInteger(){
        Funnel<Integer> funnel = Funnels.integerFunnel();
        SequenceBloomFilter<Integer> filter = new SequenceBloomFilter(10, funnel);
        filter.insert(1200);
        filter.insert(-5441);
        filter.insert(0);

        assertTrue(filter.checkMembership(1200));
        assertTrue(filter.checkMembership(-5441));
        assertFalse(filter.checkMembership(-42));

        assertEquals(filter.getApprxoimateSize(), 3);

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
