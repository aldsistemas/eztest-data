package br.com.eztest.dcv.expectation.value;

public class DifferentValue implements ExpectationValue {

    private Object value;

    public DifferentValue(Object value) {
        this.value = value;
    }

    @Override
    public boolean isSatisfiedBy(final Object o) {
        if (this.value == null) {
            return o != null;
        }
        return !this.value.equals(o);
    }

    public void setValue(final Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        String s = "DifferentValue: [" + value + "]";
        if (this.value != null) {
            s += " (" + value.getClass().getName() + ")";
        }
        return s;
    }
}
