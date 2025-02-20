package bu.cs622.sequence.generator;

import bu.cs622.sequence.generator.filters.Filter;
import bu.cs622.sequence.generator.filters.SequenceBloomFilter;
import bu.cs622.sequence.generator.filters.SequenceHashSetFilter;
import com.google.common.hash.Funnel;
import com.google.common.hash.Funnels;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.*;

import static bu.cs622.sequence.generator.Configs.THREAD_COUNT;
import static bu.cs622.sequence.generator.Configs.TOTAL_SEQUENCES;

/**
 * Name: Reaz W. Rahman
 * Course: CS 622: Advanced Programming Techniques
 * Date: 2/19/2025
 * File name: ThreadHelper.java
 * Description: This class helps to create and manage threads for generating sequences.
 */

public class ThreadHelper {
    int numThreads = THREAD_COUNT;
    Filter<String> filter;
    String filterType = Configs.FILTER_TYPE.toString();
    List<StringBuilder> output = Collections.synchronizedList(new ArrayList<>());


    public ThreadHelper() {
        if (Configs.FILTER_TYPE == Configs.FilterTypes.BLOOM_FILTER) {
            Funnel<CharSequence> funnel = Funnels.stringFunnel(java.nio.charset.StandardCharsets.UTF_8);
            filter = new SequenceBloomFilter(TOTAL_SEQUENCES, funnel);
        } else {
            filter = new SequenceHashSetFilter<>();
        }
    }

    // Create threads using the Thread class
    private void useThreadClass() throws InterruptedException {
        List<Thread> threads = new ArrayList<>();

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

    }

    // Create threads using the ExecutorService with Runnable
    private void useExecutorWithRunnable() throws InterruptedException {
        ExecutorService executor = Executors.newCachedThreadPool();
        for (int i = 1; i <= numThreads; i++) {
            executor.submit(new SequenceGenerator(i, TOTAL_SEQUENCES / THREAD_COUNT, output, filter));
        }
        shutdownExecutor(executor);

    }

    // Create threads using the ExecutorService with Future
    private void useExecutorWithFuture() throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newCachedThreadPool();
        HashMap<Integer, Future<ArrayList<StringBuilder>>> threadResultsMap = new HashMap<>();

        for (int i = 1; i <= THREAD_COUNT; i++) {
            int threadID = i;
            Callable<ArrayList<StringBuilder>> callable = () -> {
                SequenceGenerator generator = new SequenceGenerator(threadID, TOTAL_SEQUENCES / THREAD_COUNT, output, filter);
                ArrayList<StringBuilder> result;
                if (Configs.USE_FILTER) {
                    result = generator.generateUniqueSequences();
                } else {
                    result = generator.generateSequences();
                }
                if (Configs.DEBUG_MODE) {
                    generator.printSequences(result);
                }
                return result;
            };
            Future<ArrayList<StringBuilder>> future = executor.submit(callable);
            threadResultsMap.put(threadID, future);
        }

        shutdownExecutor(executor);

        if (Configs.DEBUG_MODE) {
            for (Integer key : threadResultsMap.keySet()) {
                System.out.println("Thread ID " + key + " : " + threadResultsMap.get(key).get());
            }
        }
    }

    // helper method to shut down an executor service
    private void shutdownExecutor(ExecutorService executor) throws InterruptedException, RuntimeException {
        executor.shutdown();
        while (!executor.isTerminated()) {
            executor.awaitTermination(1, TimeUnit.MILLISECONDS);
        }
        if (!executor.isShutdown()) {
            throw new RuntimeException("ThreadHelper::shutdownExecutor failed to shut down executor service");
        }
    }

    public void initiateMultiThread() throws InterruptedException, ExecutionException {
        Instant start = Instant.now();

        if (Configs.THREAD_CREATION_METHOD == Configs.ThreadCreationTypes.THREAD_CLASS) {
            useThreadClass();
        } else if (Configs.THREAD_CREATION_METHOD == Configs.ThreadCreationTypes.EXECUTOR_RUNNABLE) {
            useExecutorWithRunnable();
        } else {
            useExecutorWithFuture();
        }

        Instant end = Instant.now();
        Duration elapsed = Duration.between(start, end);

        reportResults(elapsed);
    }

    // Create a single thread to generate sequences
    public void initiateSingleThread() {
        Instant start = Instant.now();
        SequenceGenerator generator = new SequenceGenerator(1, TOTAL_SEQUENCES, output, filter);
        generator.run();
        Instant end = Instant.now();
        Duration elapsed = Duration.between(start, end);
        reportResults(elapsed);
    }

    // Report the time/memory taken by the thread(s) to generate sequences
    public void reportResults(Duration elapsed) {
        if (Configs.SINGLE_THREAD) {
            System.out.println("Single threaded total time taken: " + elapsed.toMillis() + " milliseconds");
        } else {
            System.out.println("Multi threaded total time taken: " + elapsed.toMillis() + " milliseconds");
        }
        System.out.println("output size: " + output.size());
        System.out.println("filter size: " + filter.getApprxoimateSize());
        System.out.println("Peak heap used by " + filterType + ": " + filter.getPeakMemory() / (1024 * 1024) + " MB");
    }
}
