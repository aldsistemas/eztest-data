package br.com.eztest.dcv;

import java.util.List;

public interface ContextLoader<T> {
    
    List<DataUnit<T>> loadData();
}
