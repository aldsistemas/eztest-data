package br.com.eztest.dcv;

public class IncludedItem implements DiffItem {

    private final DataUnit<?> itemIncluded;

    public IncludedItem(final DataUnit<?> itemIncluded) {
        this.itemIncluded = itemIncluded;
    }

    public DataUnit<?> getItemIncluded() {
        return this.itemIncluded;
    }
    
    @Override
    public String toString() {
        return "Item Included :\n" + this.itemIncluded;
    }
}
