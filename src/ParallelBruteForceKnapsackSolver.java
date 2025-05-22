import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.LongStream;

public class ParallelBruteForceKnapsackSolver {
    public static KnapsackResult solve(List<Item> items, int capacity) {
        long startTime = System.nanoTime();
        int n = items.size();
        long totalSubsets = 1L << n;

        ProgressTracker tracker = new ProgressTracker(totalSubsets);
        tracker.start();

        ForkJoinPool customThreadPool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());

        try {
            Future<KnapsackResult> future = customThreadPool.submit(() ->
                    LongStream.range(0, totalSubsets).parallel()
                            .collect(
                                    ResultHolder::new,
                                    (holder, i) -> {
                                        tracker.increment();
                                        processSubset(items, capacity, i, holder);
                                    },
                                    ParallelBruteForceKnapsackSolver::combineHolders
                            )
                            .toResult()
            );

            KnapsackResult result = future.get();
            long endTime = System.nanoTime();

            tracker.stop();
            return new KnapsackResult(
                    result.items(),
                    result.totalValue(),
                    result.totalSize(),
                    (endTime - startTime) / 1000,
                    (int) totalSubsets
            );

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return new KnapsackResult(Collections.emptyList(), 0, 0, 0, 0);
        } finally {
            customThreadPool.shutdown();
        }
    }

    private static void processSubset(List<Item> items, int capacity, long subset, ResultHolder holder) {
        int currentSize = 0;
        int currentValue = 0;
        List<Item> currentItems = new ArrayList<>();

        for (int j = 0; j < items.size(); j++) {
            if ((subset & (1L << j)) != 0) {
                Item item = items.get(j);
                currentItems.add(item);
                currentSize += item.size;
                currentValue += item.value;
            }
        }

        if (currentSize <= capacity && currentValue > holder.bestValue) {
            holder.update(currentItems, currentValue, currentSize);
        }
    }

    private static void combineHolders(ResultHolder h1, ResultHolder h2) {
        if (h2.bestValue > h1.bestValue) {
            h1.update(h2.bestItems, h2.bestValue, h2.bestSize);
        }
    }

    private static class ResultHolder {
        List<Item> bestItems = Collections.emptyList();
        int bestValue = 0;
        int bestSize = 0;

        void update(List<Item> items, int value, int size) {
            this.bestItems = new ArrayList<>(items);
            this.bestValue = value;
            this.bestSize = size;
        }

        KnapsackResult toResult() {
            return new KnapsackResult(bestItems, bestValue, bestSize, 0, 0);
        }
    }

    private static class ProgressTracker {
        private final AtomicLong processed = new AtomicLong(0);
        private final long total;
        private ScheduledExecutorService executor;

        public ProgressTracker(long total) {
            this.total = total;
        }

        public void increment() {
            processed.incrementAndGet();
        }

        public void start() {
            executor = Executors.newSingleThreadScheduledExecutor();
            executor.scheduleAtFixedRate(() -> {
                long done = processed.get();
                double progress = (double) done / total * 100;
                System.out.printf("\r%.2f%% complete (%,d/%,d subsets)", progress, done, total);
            }, 0, 1, TimeUnit.SECONDS);
        }

        public void stop() {
            executor.shutdown();
            System.out.printf("\r100.00%% complete (%,d/%,d subsets)%n", total, total);
        }
    }
}