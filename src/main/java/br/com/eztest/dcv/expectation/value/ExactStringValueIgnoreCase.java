package br.com.eztest.dcv.expectation.value;

public class ExactStringValueIgnoreCase implements ExpectationValue {

    private String value;

    public ExactStringValueIgnoreCase(String value) {
        this.value = value;
    }

    @Override
    public boolean isSatisfiedBy(final Object o) {
        if (this.value == null) {
            return o == null;
        }
        return this.value.equalsIgnoreCase(String.valueOf(o));
    }

    public void setValue(final String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        String s = "ExactStringValueIgnoreCase: [" + value + "]";
        if (this.value != null) {
            s += " (" + value.getClass().getName() + ")";
        }
        return s;
    }
}
