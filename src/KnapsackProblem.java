import java.io.FileNotFoundException;
import java.util.List;
import java.util.Random;

public class KnapsackProblem {
    public static void main(String[] args) {
        try {
            List<List<Item>> datasets = DatasetReader.readDatasets("knapsack.txt");
            Random random = new Random();
            int selectedDataset = random.nextInt(datasets.size());
            List<Item> items = datasets.get(selectedDataset);
            int capacity = 50;

            printDatasetInfo(selectedDataset, items, capacity);

            System.out.println("Parallel Brute Force Algorithm:");
            KnapsackResult parallelResult = ParallelBruteForceKnapsackSolver.solve(items, capacity);
            ResultPrinter.print(parallelResult);

            System.out.println("\nGreedy Algorithm:");
            KnapsackResult greedyResult = GreedyKnapsackSolver.solve(items, capacity);
            ResultPrinter.print(greedyResult);

        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + e.getMessage());
        }
    }

    private static void printDatasetInfo(int datasetIndex, List<Item> items, int capacity) {
        System.out.println("Selected dataset: " + (datasetIndex + 1));
        System.out.println("Number of items: " + items.size());
        System.out.println("Knapsack capacity: " + capacity);
        System.out.println();
    }
}