package br.com.eztest.dcv.expectation.count;

public class ExactOne implements ExpectationCount {

    private int count = 0;

    @Override
    public boolean allowMore() {
        return this.count < 1;
    }

    @Override
    public void increaseCount() {
        if (this.count != 0) {
            throw new IllegalStateException("Counter increase not allowed (already at maximum)");
        }
        this.count++;
    }

    @Override
    public boolean isSatisfied() {
        return this.count == 1;
    }

    @Override
    public void reset() {
        this.count = 0;
    }

}
