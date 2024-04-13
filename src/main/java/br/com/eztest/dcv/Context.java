package br.com.eztest.dcv;

import java.util.ArrayList;
import java.util.List;

public abstract class Context<T> {

    private final ContextLoader<T> loader;
    private DataUnitRepository<T>  repository;

    public Context(final ContextLoader<T> loader) {
        this.loader = loader;
    }

    public List<DiffItem> diff(final Context<T> postContext) {

        final List<DiffItem> ret = new ArrayList<DiffItem>();

        final DataUnitRepository<T> repPre = new DataUnitRepository<T>();
        final DataUnitRepository<T> repPos = new DataUnitRepository<T>();
        this.repository.copyTo(repPre);
        postContext.repository.copyTo(repPos);

        final List<DataUnit<T>> dusPre = repPre.getData();

        for (final DataUnit<T> d : dusPre) {
            final DataUnit<T> d2 = repPos.remove(d.getDataType(), d.getDataId());
            if (d2 == null) {
                ret.add(new RemovedItem(d));
            } else if (d.hasChanged(d2)) {
                ret.add(new ChangedItem(d, d2));
            }
        }
        final List<DataUnit<T>> dusPost = repPos.getData();
        for (final DataUnit<T> d : dusPost) {
            ret.add(new IncludedItem(d));
        }
        return ret;
    }

    public ContextLoader<T> getLoader() {
        return this.loader;
    }

    public void load() {
        this.repository = new DataUnitRepository<T>();
        final List<DataUnit<T>> cdata = this.loader.loadData();
        for (final DataUnit<T> d : cdata) {
            this.repository.add(d);
        }
    }
}
