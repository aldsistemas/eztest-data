package br.com.eztest.dcv.expectation;

import java.util.Map.Entry;

import br.com.eztest.dcv.ContextManager;
import br.com.eztest.dcv.expectation.count.ExactOne;
import br.com.eztest.dcv.expectation.value.ExpectationValue;

public class RemoveExpectationBuilder extends AbstractExpectationBuilder<RemoveExpectationBuilder> {

    private Object id;

    public RemoveExpectationBuilder(final ExpectationFactory factory, final ContextManager contextManager, final String dataType) {
        super(factory, contextManager, dataType);
    }

    public void setId(final Object id) {
        this.id = id;
    }

    @Override
    protected Expectation buildExpectation() {
        final RemoveExpectation e = new RemoveExpectation(getDataType(), new ExactOne());
        for (final Entry<String, ExpectationValue> v : getValues().entrySet()) {
            e.addExpectation(v.getKey(), v.getValue());
        }
        e.setDataId(id);
        return e;
    }
}
