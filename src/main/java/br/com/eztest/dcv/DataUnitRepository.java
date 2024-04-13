package br.com.eztest.dcv;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataUnitRepository<T> {

    private final Map<String, Map<Object, DataUnit<T>>> data = new HashMap<String, Map<Object, DataUnit<T>>>();

    public void add(final DataUnit<T> du) {

        Map<Object, DataUnit<T>> d = this.data.get(du.getDataType());
        if (d == null) {
            d = new HashMap<Object, DataUnit<T>>();
            this.data.put(du.getDataType(), d);
        }
        d.put(du.getDataId(), du);
    }

    public void addAll(final Collection<DataUnit<T>> dus) {
        for (final DataUnit<T> du : dus) {
            add(du);
        }
    }

    public void copyTo(final DataUnitRepository<T> rep) {
        rep.addAll(getData());
    }

    public DataUnit<T> get(final String dataType, final Object dataId) {
        final Map<Object, DataUnit<T>> m = this.data.get(dataType);
        if (m != null) {
            return m.get(dataId);
        }
        return null;
    }

    public List<DataUnit<T>> getByDataType(final String dataType) {

        final ArrayList<DataUnit<T>> ret = new ArrayList<DataUnit<T>>();
        final Map<Object, DataUnit<T>> m = this.data.get(dataType);
        if (m != null) {
            ret.addAll(m.values());
        }
        return ret;
    }

    public List<DataUnit<T>> getData() {
        final ArrayList<DataUnit<T>> ret = new ArrayList<DataUnit<T>>();
        for (final Map<Object, DataUnit<T>> m : this.data.values()) {
            ret.addAll(m.values());
        }
        return ret;

    }

    public DataUnit<T> remove(final String dataType, final Object dataId) {
        final Map<Object, DataUnit<T>> m = this.data.get(dataType);
        if (m != null) {
            return m.remove(dataId);
        }
        return null;
    }
}
