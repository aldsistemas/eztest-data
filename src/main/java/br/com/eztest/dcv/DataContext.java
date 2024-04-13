package br.com.eztest.dcv;

public abstract class DataContext<T> extends Context<T> {

    public DataContext(ContextLoader<T> loader) {
        super(loader);
    }
}
