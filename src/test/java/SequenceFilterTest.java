import bu.cs622.sequence.generator.filters.SequenceBloomFilter;
import org.junit.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SequenceFilterTest {

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
        Thread t1 = new Thread(() -> {
                    filter.insert("apple");
                    filter.insert("orange");
                    filter.insert("banana");
                });

        Thread t2 = new Thread(() -> {
            filter.insert("mango");
            filter.insert("guava");
            filter.insert("banana"); // duplicate
        });

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        assertTrue(filter.checkMembership("apple"));
        assertTrue(filter.checkMembership("mango"));
        assertFalse(filter.checkMembership("grapes"));

        assertEquals(filter.getApprxoimateSize(), 5);

    }
}
