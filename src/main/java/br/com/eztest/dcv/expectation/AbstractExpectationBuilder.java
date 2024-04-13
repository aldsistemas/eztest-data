package br.com.eztest.dcv.expectation;

import java.util.LinkedHashMap;
import java.util.Map;

import br.com.eztest.dcv.ContextManager;
import br.com.eztest.dcv.expectation.value.DifferentValue;
import br.com.eztest.dcv.expectation.value.ExactStringValueIgnoreCase;
import br.com.eztest.dcv.expectation.value.ExactValue;
import br.com.eztest.dcv.expectation.value.ExpectationValue;

public abstract class AbstractExpectationBuilder<T extends AbstractExpectationBuilder<?>> {

    private final ExpectationFactory<T>         factory;
    private final ContextManager<T> contextManager;
    private final String                        dataType;
    private final Map<String, ExpectationValue> values = new LinkedHashMap<String, ExpectationValue>();
    private boolean                             closed = false;

    public AbstractExpectationBuilder(final ExpectationFactory<T> factory, final ContextManager<T> contextManager, final String dataType) {
        this.factory = factory;
        this.contextManager = contextManager;
        this.dataType = dataType;
    }

    public T eq(final String property, final Object value) {
        add(property, new ExactValue(value));
        return (T) this;
    }
    
    public T eqi(final String property, final String value) {
        add(property, new ExactStringValueIgnoreCase(value));
        return (T) this;
    }


    public boolean isClosed() {
        return this.closed;
    }

    public T ne(final String property, final Object value) {
        add(property, new DifferentValue(value));
        return (T) this;
    }

    public final ExpectationFactory<T> next() {

        final Expectation e = buildExpectation();
        getContextManager().add(e);
        this.closed = true;
        return getFactory();
    }

    protected void add(final String property, final ExpectationValue value) {
        this.values.put(property, value);
    }

    protected abstract Expectation buildExpectation();

    protected ContextManager<T> getContextManager() {
        return this.contextManager;
    }

    protected String getDataType() {
        return this.dataType;
    }

    protected ExpectationFactory<T> getFactory() {
        return this.factory;
    }

    protected Map<String, ExpectationValue> getValues() {
        return this.values;
    }
}
