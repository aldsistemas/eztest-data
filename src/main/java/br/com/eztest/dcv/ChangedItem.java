package br.com.eztest.dcv;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class ChangedItem<T> implements DiffItem {

    private final DataUnit<T> postState;
    private final DataUnit<T> preState;

    public ChangedItem(final DataUnit<T> preState, final DataUnit<T> postState) {
        this.preState = preState;
        this.postState = postState;
    }

    public DataUnit<T> getPostState() {
        return this.postState;
    }

    public DataUnit<T> getPreState() {
        return this.preState;
    }

    @Override
    public String toString() {
        final Set<Entry<String, Object>> preValues = this.preState.getValues().entrySet();
        final Set<Entry<String, Object>> postValues = this.postState.getValues().entrySet();
        final Map<String, Object> changes = new HashMap<String, Object>();
        for (final Entry<String, Object> e : postValues) {
            if (!preValues.contains(e)) {
                changes.put(e.getKey(), e.getValue());
            }
        }
        return "Item changed:\n" + this.preState + "\nTo:\n" + this.postState + "\nChanges:\n" + changes;
    }
}
