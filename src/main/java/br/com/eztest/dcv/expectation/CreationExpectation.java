package br.com.eztest.dcv.expectation;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import br.com.eztest.dcv.DataUnit;
import br.com.eztest.dcv.IncludedItem;
import br.com.eztest.dcv.DiffItem;
import br.com.eztest.dcv.expectation.count.ExpectationCount;
import br.com.eztest.dcv.expectation.value.ExpectationValue;

public class CreationExpectation implements Expectation {

    private final String                        dataType;
    private Object                              dataId;

    private final Map<String, ExpectationValue> expecs = new HashMap<String, ExpectationValue>();
    private final ExpectationCount              count;

    public CreationExpectation(final String dataType, final ExpectationCount count) {
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

    public void setDataId(final Object dataId) {
        this.dataId = dataId;
    }

    @Override
    public String toString() {

        return "Expectation Inclusion of:\nDataType: " + this.dataType + "\nValues:\n" + formataExpectations();
    }

    @Override
    public boolean register(final DiffItem diff) {
        if (!this.count.allowMore()) {
            return false;
        }
        if (!(diff instanceof IncludedItem)) {
            return false;
        }
        final IncludedItem item = (IncludedItem) diff;
        final DataUnit<?> dataUnit = item.getItemIncluded();
        if (!this.dataType.equals(dataUnit.getDataType())) {
            return false;
        }
        if (this.dataId != null && !this.dataId.equals(dataUnit.getDataId())) {
            return false;
        }
        for (final Entry<String, ExpectationValue> es : this.expecs.entrySet()) {
            final String propertyName = es.getKey();
            final ExpectationValue expectation = es.getValue();
            if (!dataUnit.hasProperty(propertyName) || !expectation.isSatisfiedBy(dataUnit.getValue(propertyName))) {
                return false;
            }
        }
        this.count.increaseCount();
        return true;
    }

    private String formataExpectations() {
        final StringBuilder ret = new StringBuilder();
        for (final Entry<String, ExpectationValue> e : this.expecs.entrySet()) {
            ret.append(e.getKey()).append(" => ").append(e.getValue()).append("\n");
        }
        return ret.toString();
    }

    @Override
    public void reset() {
        this.count.reset();
    }

    @Override
    public ExpectationType getType() {
        return ExpectationType.ADD;
    }

    @Override
    public boolean canMergeWith(Expectation other) {
        return false;
    }

    @Override
    public void mergeWith(Expectation other) {
        throw new UnsupportedOperationException("CreationExpectation does not support expectation merge");
    }

}
