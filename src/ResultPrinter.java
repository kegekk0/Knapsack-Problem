public class ResultPrinter {
    public static void print(KnapsackResult result) {
        System.out.println("Items in knapsack:");
        for (Item item : result.items()) {
            System.out.printf("[%d, %d, %d] ", item.nr, item.size, item.value);
        }
        System.out.println();
        System.out.println("Total value: " + result.totalValue());
        System.out.println("Total size used: " + result.totalSize());
        System.out.println("Execution time (microseconds): " + result.executionTime());
        System.out.println("Subsets investigated: " + result.subsetsInvestigated());
    }
}