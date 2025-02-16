import org.junit.Test;

import bu.cs622.sequence.generator.SequenceFilter;

import static org.junit.jupiter.api.Assertions.*;

public class TestSequenceFilter {

    @Test
    public void testBloomFilter(){
        SequenceFilter filter = new SequenceFilter(10);
        filter.insert("apple");
        filter.insert("orange");
        filter.insert("banana");

        assertTrue(filter.checkMembership("apple"));
        assertTrue(filter.checkMembership("banana"));
        assertFalse(filter.checkMembership("grapes"));

        assertEquals(filter.getApprxoimateSize(), 3);

    }
}
