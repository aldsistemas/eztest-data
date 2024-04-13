package br.com.eztest.dcv.expectation.count;

public interface ExpectationCount {

    boolean allowMore();

    void increaseCount();

    boolean isSatisfied();

    void reset();
}
