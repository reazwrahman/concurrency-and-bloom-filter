package bu.cs622.sequence.generator;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.*;

import static bu.cs622.sequence.generator.Configs.THREAD_COUNT;
import static bu.cs622.sequence.generator.Configs.TOTAL_SEQUENCES;

public class ThreadHelper {
    int numThreads = THREAD_COUNT;
    SequenceFilter filter = new SequenceFilter(TOTAL_SEQUENCES);
    List<StringBuilder> output = Collections.synchronizedList(new ArrayList<>());

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

    private void useExecutorWithRunnable() throws InterruptedException {
        ExecutorService executor = Executors.newCachedThreadPool();
         for (int i = 1; i <= numThreads; i++) {
            executor.submit(new SequenceGenerator(i, TOTAL_SEQUENCES / THREAD_COUNT, output, filter));
        }
        shutdownExecutor(executor);

    }

    private void useExecutorWithFuture() throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newCachedThreadPool();
        HashMap<Integer, Future<ArrayList<StringBuilder>>> threadResultsMap = new HashMap<>();

        for (int i=1; i<= THREAD_COUNT; i++) {
            int threadID = i;
            Callable<ArrayList<StringBuilder>> callable = () -> {
                SequenceGenerator generator = new SequenceGenerator(threadID, TOTAL_SEQUENCES / THREAD_COUNT, output, filter);
                ArrayList<StringBuilder> output;
                if (Configs.USE_FILTER) {
                    output = generator.generateUniqueSequences();
                }else {
                    output = generator.generateSequences();
                }
                if (Configs.DEBUG_MODE) {
                    generator.printSequences(output);
                }
                return output;
            };
            Future<ArrayList<StringBuilder>> future =  executor.submit(callable);
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
    private void shutdownExecutor(ExecutorService executor) throws InterruptedException {
        executor.shutdown();
        while (!executor.isTerminated()) {
            executor.awaitTermination(1, TimeUnit.MILLISECONDS);
        }
        if(!executor.isShutdown()) {
            throw new RuntimeException("ThreadHelper::shutdownExecutor failed to shut down executor service");
        }
    }

    public void initiateMultiThread() throws InterruptedException, ExecutionException {
        Instant start = Instant.now();

        if (Configs.THREAD_TYPE == Configs.ThreadCreationTypes.THREAD_CLASS) {
            useThreadClass();
        }else if (Configs.THREAD_TYPE == Configs.ThreadCreationTypes.EXECUTOR_RUNNABLE) {
            useExecutorWithRunnable();
        }else {
            useExecutorWithFuture();
        }

        Instant end = Instant.now();
        Duration elapsed = Duration.between(start, end);

        System.out.println("Multi threaded total time taken: " + elapsed.toMillis() + " milliseconds");
        System.out.println("output size: " + output.size());
        System.out.println("filter size: " + filter.getApprxoimateSize());
        System.out.println("Peak heap used by bloom filter: " + filter.peakMemory/ (1024 * 1024) + " MB");

    }

    public void initiateSingleThread(){
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
    }
}
