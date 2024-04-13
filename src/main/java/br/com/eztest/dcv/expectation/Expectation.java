package br.com.eztest.dcv.expectation;

import br.com.eztest.dcv.DiffItem;

public interface Expectation {

    public enum ExpectationType {
        ADD, REMOVE, CHANGE;
    }

    ExpectationType getType();

    boolean register(DiffItem diff);

    boolean isSatisfied();

    void reset();

    boolean canMergeWith(Expectation other);

    void mergeWith(Expectation other);
}
