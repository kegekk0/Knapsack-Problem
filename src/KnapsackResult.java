import java.util.List;

public record KnapsackResult(
        List<Item> items,
        int totalValue,
        int totalSize,
        long executionTime,
        int subsetsInvestigated) { }