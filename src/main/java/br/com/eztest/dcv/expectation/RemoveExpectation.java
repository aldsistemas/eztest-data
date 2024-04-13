package br.com.eztest.dcv.expectation;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import br.com.eztest.dcv.DataUnit;
import br.com.eztest.dcv.DiffItem;
import br.com.eztest.dcv.RemovedItem;
import br.com.eztest.dcv.expectation.count.ExpectationCount;
import br.com.eztest.dcv.expectation.value.ExpectationValue;

public class RemoveExpectation implements Expectation {

    private final String                        dataType;
    private Object                              dataId;

    private final Map<String, ExpectationValue> expecs = new HashMap<String, ExpectationValue>();

    private final ExpectationCount              count;

    public RemoveExpectation(final String dataType, final ExpectationCount count) {
        this.dataType = dataType;
        this.count = count;
    }

    @Override
    public ExpectationType getType() {
        return ExpectationType.REMOVE;
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

        return "Expectation Removal of:\nDataType: " + this.dataType + "\nDataID: " + this.dataId + "\nValues:\n" + formataExpectations();
    }

    @Override
    public boolean register(final DiffItem diff) {
        if (!this.count.allowMore()) {
            return false;
        }
        if (!(diff instanceof RemovedItem)) {
            return false;
        }
        final RemovedItem item = (RemovedItem) diff;
        final DataUnit<?> dataUnit = item.getRemovedItem();
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
    public boolean canMergeWith(Expectation other) {
        return false;
    }

    @Override
    public void mergeWith(Expectation other) {
        throw new UnsupportedOperationException("RemoveExpectation does not support expectation merge");
    }

}
