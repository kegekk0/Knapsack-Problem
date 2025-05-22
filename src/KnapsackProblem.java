import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class KnapsackProblem {

    static class Item {
        int nr;
        int size;
        int value;
        double ratio;

        public Item(int nr, int size, int value) {
            this.nr = nr;
            this.size = size;
            this.value = value;
            this.ratio = (double) value / size;
        }
    }

    static class KnapsackResult {
        List<Item> items;
        int totalValue;
        int totalSize;
        long executionTime;
        int subsetsInvestigated;

        public KnapsackResult(List<Item> items, int totalValue, int totalSize, long executionTime, int subsetsInvestigated) {
            this.items = items;
            this.totalValue = totalValue;
            this.totalSize = totalSize;
            this.executionTime = executionTime;
            this.subsetsInvestigated = subsetsInvestigated;
        }
    }

    public static void main(String[] args) {
        try {
            // Read all datasets from file
            List<List<Item>> datasets = readDatasets();

            // Randomly select a dataset
            Random random = new Random();
            int selectedDataset = random.nextInt(15);
            List<Item> items = datasets.get(selectedDataset);
            int capacity = 50;

            System.out.println("Selected dataset: " + (selectedDataset + 1));
            System.out.println("Number of items: " + items.size());
            System.out.println("Knapsack capacity: " + capacity);
            System.out.println();

            // Brute force solution
            System.out.println("Brute Force Algorithm:");
            KnapsackResult bruteForceResult = bruteForceKnapsack(items, capacity);
            printResult(bruteForceResult);

            System.out.println();

            // Greedy solution
            System.out.println("Greedy Algorithm:");
            KnapsackResult greedyResult = greedyKnapsack(items, capacity);
            printResult(greedyResult);

        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + e.getMessage());
        }
    }

    private static List<List<Item>> readDatasets() throws FileNotFoundException {
        List<List<Item>> datasets = new ArrayList<>();
        Scanner scanner = new Scanner(new File("knapsack.txt"));

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.startsWith("dataset")) {
                // Read sizes
                String sizesLine = scanner.nextLine().split("=")[1].trim();
                sizesLine = sizesLine.substring(1, sizesLine.length() - 1).trim();
                // Split by comma followed by optional whitespace
                String[] sizes = sizesLine.split(",\\s*");

                // Read values
                String valuesLine = scanner.nextLine().split("=")[1].trim();
                valuesLine = valuesLine.substring(1, valuesLine.length() - 1).trim();
                // Split by comma followed by optional whitespace
                String[] values = valuesLine.split(",\\s*");

                // Verify we have matching numbers of sizes and values
                if (sizes.length != values.length) {
                    System.err.println("Warning: Mismatched sizes and values in dataset");
                    continue;
                }

                // Create items list
                List<Item> items = new ArrayList<>();
                for (int i = 0; i < sizes.length; i++) {
                    try {
                        int size = Integer.parseInt(sizes[i].trim());
                        int value = Integer.parseInt(values[i].trim());
                        items.add(new Item(i + 1, size, value));
                    } catch (NumberFormatException e) {
                        System.err.println("Error parsing number: " + e.getMessage());
                        System.err.println("Size: '" + sizes[i] + "', Value: '" + values[i] + "'");
                    }
                }

                datasets.add(items);
            }
        }

        scanner.close();
        return datasets;
    }

    private static KnapsackResult bruteForceKnapsack(List<Item> items, int capacity) {
        long startTime = System.nanoTime();
        int n = items.size();
        int maxValue = 0;
        int bestSize = 0;
        List<Item> bestItems = new ArrayList<>();
        int subsetsInvestigated = 0;
        System.out.println("Starting with " + n + " items");

        // Generate all possible subsets (2^n possibilities)
        for (int i = 0; i < (1 << n); i++) {
            int currentSize = 0;
            int currentValue = 0;
            List<Item> currentItems = new ArrayList<>();

            for (int j = 0; j < n; j++) {
                if ((i & (1 << j)) > 0) {
                    currentItems.add(items.get(j));
                    currentSize += items.get(j).size;
                    currentValue += items.get(j).value;
                }
            }

            subsetsInvestigated++;

            if (currentSize <= capacity && currentValue > maxValue) {
                maxValue = currentValue;
                bestSize = currentSize;
                bestItems = new ArrayList<>(currentItems);
            }
        }

        long endTime = System.nanoTime();
        long executionTime = (endTime - startTime) / 1000; // microseconds

        return new KnapsackResult(bestItems, maxValue, bestSize, executionTime, subsetsInvestigated);
    }

    private static KnapsackResult greedyKnapsack(List<Item> items, int capacity) {
        long startTime = System.nanoTime();
        List<Item> sortedItems = new ArrayList<>(items);

        // Sort items by value-to-size ratio in descending order
        Collections.sort(sortedItems, new Comparator<Item>() {
            @Override
            public int compare(Item a, Item b) {
                return Double.compare(b.ratio, a.ratio);
            }
        });

        int currentSize = 0;
        int totalValue = 0;
        List<Item> selectedItems = new ArrayList<>();
        int subsetsInvestigated = 0;

        for (Item item : sortedItems) {
            subsetsInvestigated++;
            if (currentSize + item.size <= capacity) {
                selectedItems.add(item);
                currentSize += item.size;
                totalValue += item.value;
            }
        }

        long endTime = System.nanoTime();
        long executionTime = (endTime - startTime) / 1000; // microseconds

        return new KnapsackResult(selectedItems, totalValue, currentSize, executionTime, subsetsInvestigated);
    }

    private static void printResult(KnapsackResult result) {
        System.out.println("Items in knapsack:");
        for (Item item : result.items) {
            System.out.printf("[%d, %d, %d] ", item.nr, item.size, item.value);
        }
        System.out.println();
        System.out.println("Total value: " + result.totalValue);
        System.out.println("Total size used: " + result.totalSize);
        System.out.println("Execution time (microseconds): " + result.executionTime);
        System.out.println("Subsets investigated: " + result.subsetsInvestigated);
    }
}