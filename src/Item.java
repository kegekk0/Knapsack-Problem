public class Item {
    public final int nr;
    public final int size;
    public final int value;
    public final double ratio;

    public Item(int nr, int size, int value) {
        this.nr = nr;
        this.size = size;
        this.value = value;
        this.ratio = (double) value / size;
    }
}