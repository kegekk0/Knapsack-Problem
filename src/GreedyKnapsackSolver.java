import java.util.ArrayList;
import java.util.List;

public class GreedyKnapsackSolver {
    public static KnapsackResult solve(List<Item> items, int capacity) {
        long startTime = System.nanoTime();
        List<Item> sortedItems = new ArrayList<>(items);
        sortedItems.sort((a, b) -> Double.compare(b.ratio, a.ratio));

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
        long executionTime = (endTime - startTime) / 1000;

        return new KnapsackResult(selectedItems, totalValue, currentSize,
                executionTime, subsetsInvestigated);
    }
}