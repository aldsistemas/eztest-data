package br.com.eztest.dcv;

public class RemovedItem implements DiffItem {

    private final DataUnit<?> removedItem;

    public RemovedItem(final DataUnit<?> removedItem) {
        this.removedItem = removedItem;
    }

    public DataUnit<?> getRemovedItem() {
        return this.removedItem;
    }
    @Override
    public String toString() {
        return "Item Removed :\n" + this.removedItem;
    }
}
