package br.com.eztest.dcv.expectation.value;

import java.math.BigDecimal;

public class ExactValue implements ExpectationValue {

    private Object value;

    public ExactValue(Object value) {
        this.value = value;
    }

    @Override
    public boolean isSatisfiedBy(final Object o) {
        if (this.value == null) {
            return o == null;
        }
        if (this.value instanceof BigDecimal && o instanceof BigDecimal) {
            BigDecimal value1 = (BigDecimal) this.value;
            BigDecimal value2 = (BigDecimal) o;
            return value1.compareTo(value2) == 0;
        }
        return this.value.equals(o);
    }

    public void setValue(final Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        String s = "ExactValue: [" + value + "]";
        if (this.value != null) {
            s += " (" + value.getClass().getName() + ")";
        }
        return s;
    }
}
