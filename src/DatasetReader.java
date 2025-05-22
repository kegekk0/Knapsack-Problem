import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class DatasetReader {
    public static List<List<Item>> readDatasets(String filename) throws FileNotFoundException {
        List<List<Item>> datasets = new ArrayList<>();
        Scanner scanner = new Scanner(new File(filename));

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.startsWith("dataset")) {
                List<Item> items = readDataset(scanner);
                if (!items.isEmpty()) {
                    datasets.add(items);
                }
            }
        }
        scanner.close();
        return datasets;
    }

    private static List<Item> readDataset(Scanner scanner) {
        String sizesLine = scanner.nextLine().split("=")[1].trim();
        sizesLine = sizesLine.substring(1, sizesLine.length() - 1).trim();
        String[] sizes = sizesLine.split(",\\s*");

        String valuesLine = scanner.nextLine().split("=")[1].trim();
        valuesLine = valuesLine.substring(1, valuesLine.length() - 1).trim();
        String[] values = valuesLine.split(",\\s*");

        if (sizes.length != values.length) {
            System.err.println("Warning: Mismatched sizes and values in dataset");
            return Collections.emptyList();
        }

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
        return items;
    }
}