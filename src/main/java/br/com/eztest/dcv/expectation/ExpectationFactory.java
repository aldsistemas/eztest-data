package br.com.eztest.dcv.expectation;

import br.com.eztest.dcv.ContextManager;

public class ExpectationFactory<T> {

    private final ContextManager<T> contextManager;
    private AbstractExpectationBuilder<?> current;

    public ExpectationFactory(final ContextManager<T> contextManager) {
        this.contextManager = contextManager;
    }

    public ChangeExpectationBuilder change(final String dataType, final Object id) {
        final ChangeExpectationBuilder ret = new ChangeExpectationBuilder(this, this.contextManager, dataType);
        ret.setId(id);
        installCurrent(ret);
        return ret;
    }

    public CreationExpectationBuilder creation(final String dataType) {
        final CreationExpectationBuilder ret = new CreationExpectationBuilder(this, this.contextManager, dataType);
        installCurrent(ret);
        return ret;
    }

    public CreationExpectationBuilder creation(final String dataType, final Object id) {
        final CreationExpectationBuilder ret = new CreationExpectationBuilder(this, this.contextManager, dataType);
        ret.setId(id);
        installCurrent(ret);
        return ret;
    }

    public void flush() {
        if (this.current != null && !this.current.isClosed()) {
            this.current.next();
        }
    }

    public RemoveExpectationBuilder removal(final String dataType) {
        final RemoveExpectationBuilder ret = new RemoveExpectationBuilder(this, this.contextManager, dataType);
        installCurrent(ret);
        return ret;
    }

    public RemoveExpectationBuilder removal(final String dataType, final Object id) {

        final RemoveExpectationBuilder ret = new RemoveExpectationBuilder(this, this.contextManager, dataType);
        ret.setId(id);
        installCurrent(ret);
        return ret;
    }

    private void installCurrent(final AbstractExpectationBuilder<?> curr) {
        flush();
        this.current = curr;
    }
}
