package br.com.eztest.dcv;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class FieldDataMapper implements DataValuesMapper {

    private static Map<Class<?>, List<Field>> fields = new HashMap<Class<?>, List<Field>>();

    public abstract Object getId(Object o);

    @Override
    public Object getValue(final String key, final DataUnit<Object> data) {

        if (key == null) {
            throw new IllegalArgumentException("Chave invalida: null");
        }
        final String[] parts = key.split("[.]");

        final Map<String, Object> vals = getValues(data);
        if (!vals.containsKey(parts[0])) {
            throw new IllegalArgumentException("Objeto nao contem o campo [" + parts[0] + "]");
        }
        if (parts.length == 1) {
            return vals.get(parts[0]);
        }
        Object val = vals.get(parts[0]);
        for (int i = 1; i < parts.length; i++) {
            val = getFieldValue(parts[i], val);
        }
        return val;
    }

    @Override
    public Map<String, Object> getValues(final DataUnit<Object> data) {

        final Object data2 = data.getData();
        if (data2 == null) {
            return new HashMap<String, Object>();
        }
        final Class<? extends Object> class1 = data2.getClass();

        final List<Field> fds = getFields(class1);
        final Map<String, Object> ret = new HashMap<String, Object>();
        for (final Field f : fds) {
            try {
                ret.put(f.getName(), f.get(data2));
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        }
        return ret;
    }

    @Override
    public boolean hasProperty(final String key, final DataUnit<Object> data) {

        if (key == null) {
            return false;
        }
        final String[] parts = key.split("[.]");

        final Map<String, Object> vals = getValues(data);
        if (!vals.containsKey(parts[0])) {
            return false;
        }
        if (parts.length == 1) {
            return true;
        }
        Object val = vals.get(parts[0]);

        // Este laco permite que sejam validados todos os atributos da navegaccao ate o penultimo. Se o getFieldValue do
        // ultimo retornar null, isso nao faz o metodo retornar false
        for (int i = 1; i < parts.length; i++) {
            if (val == null) {
                return false;
            }
            val = getFieldValue(parts[i], val);
        }
        return true;
    }

    private List<Field> extractFields(final Class<? extends Object> class1) {

        final Map<String, Field> mapa = new HashMap<String, Field>();
        Class<?> c = class1;
        while (!c.equals(Object.class)) {

            final Field[] fds = c.getDeclaredFields();
            for (final Field f : fds) {
                if (!mapa.containsKey(f.getName())) {
                    mapa.put(f.getName(), f);
                    f.setAccessible(true);
                }
            }
            c = c.getSuperclass();
        }
        return new ArrayList<Field>(mapa.values());
    }

    protected List<Field> getFields(final Class<? extends Object> class1) {
        List<Field> ret = fields.get(class1);
        if (ret == null) {
            ret = extractFields(class1);
            fields.put(class1, ret);
        }
        return ret;
    }

    private Object getFieldValue(final String fieldName, final Object obj) {
        if (obj == null) {
            throw new IllegalArgumentException("Objeto invalido: null");
        }
        if (fieldName == null) {
            throw new IllegalArgumentException("FieldName invalido: null");
        }

        try {
            Class<?> classType = obj.getClass();
            while (classType != null && classType != Object.class) {
                try {
                    final Field f = classType.getDeclaredField(fieldName);
                    f.setAccessible(true);
                    return f.get(obj);
                } catch (NoSuchFieldException e) {
                    classType = classType.getSuperclass();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        throw new RuntimeException(new NoSuchFieldException());
    }
}
