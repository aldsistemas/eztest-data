package br.com.eztest.dcv;

import java.lang.reflect.Field;

public class FieldIdGenerator<T> implements ContextIdGenerator<T> {

    private final Field method;

    public FieldIdGenerator(Field field) {
        this.method = field;
    }
    
    @Override
    public Object generateId(T o) {
        
        Object invoke;
        try {
            invoke = this.method.get(o);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return invoke;
    }


}
