package br.com.eztest.dcv;

public interface ContextIdGenerator<T> {

    Object generateId(T o);
}
