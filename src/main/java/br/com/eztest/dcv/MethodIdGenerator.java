package br.com.eztest.dcv;

import java.lang.reflect.Method;

public class MethodIdGenerator<T> implements ContextIdGenerator<T> {

    private final Method method;

    public MethodIdGenerator(Method method) {
        this.method = method;
    }

    @Override
    public Object generateId(T o) {

        Object invoke;
        try {
            invoke = this.method.invoke(o, null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return invoke;
    }

}
