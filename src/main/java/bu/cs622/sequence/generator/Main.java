package bu.cs622.sequence.generator;

import javax.management.InvalidAttributeValueException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static bu.cs622.sequence.generator.Configs.THREAD_COUNT;
import static bu.cs622.sequence.generator.Configs.TOTAL_SEQUENCES;

public class Main {

    public static void main(String[] args) throws InterruptedException, InvalidAttributeValueException {
        validateConfigs();
        if(Configs.SINGLE_THREAD) {
            runSingleThreaded();
        }
        if (Configs.MULTI_THREAD) {
            runMultiThreaded();
        }
    }

    public static void validateConfigs() throws InvalidAttributeValueException {
        if (Configs.USE_FILTER && TOTAL_SEQUENCES > 1000000) {
            throw new InvalidAttributeValueException("Only 1 Million unique sequences can be generated");
        }
    }

    public static Duration runSingleThreaded(){
        Instant start = Instant.now();
        List<StringBuilder> output = new ArrayList<>();
        SequenceFilter filter = new SequenceFilter(TOTAL_SEQUENCES);

        SequenceGenerator generator = new SequenceGenerator(1, TOTAL_SEQUENCES, output, filter);
        generator.run();
        Instant end = Instant.now();
        Duration elapsed = Duration.between(start, end);

        System.out.println("Single threaded total time taken: " + elapsed.toMillis() + " milliseconds");
        System.out.println("output size: " + output.size());
        System.out.println("filter size: " + filter.getApprxoimateSize());
        System.out.println("Peak heap used by bloom filter: " + filter.peakMemory/ (1024 * 1024) + " MB");
        return elapsed;
    }

    public static Duration runMultiThreaded() throws InterruptedException {
        Instant start = Instant.now();
        int numThreads = THREAD_COUNT;
        List<Thread> threads = new ArrayList<>();
        List<StringBuilder> output = Collections.synchronizedList(new ArrayList<>());
        SequenceFilter filter = new SequenceFilter(TOTAL_SEQUENCES);

        for (int i = 1; i <= numThreads; i++) {
            Thread thread = new Thread(new SequenceGenerator(i, TOTAL_SEQUENCES / THREAD_COUNT, output, filter));
            threads.add(thread);
            thread.start();
        }

        // Wait for all threads to complete, before calculating duration
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw e;
            }
        }

        Instant end = Instant.now();
        Duration elapsed = Duration.between(start, end);

        System.out.println("Multi threaded total time taken: " + elapsed.toMillis() + " milliseconds");
        System.out.println("output size: " + output.size());
        System.out.println("filter size: " + filter.getApprxoimateSize());
        System.out.println("Peak heap used by bloom filter: " + filter.peakMemory/ (1024 * 1024) + " MB");
        return elapsed;
    }
}
