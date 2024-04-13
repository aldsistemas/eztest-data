package br.com.eztest.dcv.expectation;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import br.com.eztest.dcv.ChangedItem;
import br.com.eztest.dcv.DataUnit;
import br.com.eztest.dcv.DiffItem;
import br.com.eztest.dcv.expectation.count.ExpectationCount;
import br.com.eztest.dcv.expectation.value.ExpectationValue;

public class ChangeExpectation implements Expectation {

    private final String                        dataType;
    private Object                              dataId;

    private final Map<String, ExpectationValue> expecs = new LinkedHashMap<String, ExpectationValue>();

    private final ExpectationCount              count;

    @Override
    public ExpectationType getType() {
        return ExpectationType.CHANGE;
    }

    public ChangeExpectation(final String dataType, final ExpectationCount count) {
        this.dataType = dataType;
        this.count = count;
    }

    public void addExpectation(final String property, final ExpectationValue val) {
        this.expecs.put(property, val);
    }

    @Override
    public boolean isSatisfied() {
        return this.count.isSatisfied();
    }

    @Override
    public boolean register(final DiffItem diff) {
        if (!this.count.allowMore()) {
            return false;
        }
        if (!(diff instanceof ChangedItem<?>)) {
            return false;
        }
        final ChangedItem<Object> item = (ChangedItem<Object>) diff;
        final DataUnit<Object> dataUnit1 = item.getPreState();
        final DataUnit<Object> dataUnit2 = item.getPostState();
        if (!this.dataType.equals(dataUnit1.getDataType())) {
            return false;
        }
        if (!this.dataType.equals(dataUnit2.getDataType())) {
            return false;
        }
        if (this.dataId != null && !this.dataId.equals(dataUnit1.getDataId())) {
            return false;
        }
        if (this.dataId != null && !this.dataId.equals(dataUnit2.getDataId())) {
            return false;
        }
        // A verificacao de alteracao eh feita em duas partes.
        // Uma parte verifica se o dataUnit 2 esta com o valor esperado.
        // A outra verifica se o valor da variavel no dataunit2 esta diferente do equivalendo no dataunit1
        for (final Entry<String, ExpectationValue> es : this.expecs.entrySet()) {

            final ExpectationValue expectation = es.getValue();
            final String changedProperty = es.getKey();
            if (!dataUnit2.hasProperty(changedProperty) || !expectation.isSatisfiedBy(dataUnit2.getValue(changedProperty))) {
                return false;
            }
//            if (dataUnit1.hasProperty(changedProperty) && expectation.isSatisfiedBy(dataUnit1.getValue(changedProperty))) {
//                return false;
//            }
        }
        this.count.increaseCount();
        return true;
    }

    @Override
    public void reset() {
        this.count.reset();
    }

    public void setDataId(final Object dataId) {
        this.dataId = dataId;
    }

    @Override
    public String toString() {

        return "Expectation Change from:\nDataType: " + this.dataType + "\nDataID: " + this.dataId + "\nTo Values:\n"
                + formataExpectations();
    }

    private boolean compare(final Object v1, final Object v2) {
        if (v1 == null) {
            return v2 == null;
        }
        return v1.equals(v2);
    }

    private String formataExpectations() {
        final StringBuilder ret = new StringBuilder();
        for (final Entry<String, ExpectationValue> e : this.expecs.entrySet()) {
            ret.append(e.getKey()).append(" => ").append(e.getValue()).append("\n");
        }
        return ret.toString();
    }

    @Override
    public boolean canMergeWith(Expectation other) {
        if (!(other instanceof ChangeExpectation)) {
            return false;
        }
        ChangeExpectation otherExpec = (ChangeExpectation) other;
        if (otherExpec.dataType.equals(this.dataType) && dataId != null && dataId.equals(otherExpec.dataId)) {
            return true;
        }
        return false;
    }

    @Override
    public void mergeWith(Expectation other) {
        if (!canMergeWith(other)){
            throw new IllegalArgumentException("Cannot merge with "+other);
        }
        ChangeExpectation ce = (ChangeExpectation) other;
        this.expecs.putAll(ce.expecs);
    }

}
