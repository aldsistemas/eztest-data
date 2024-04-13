package br.com.eztest.dcv;

public interface ContextFactory<T> {

    Context<T> createContext();
}
