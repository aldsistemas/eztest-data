package br.com.eztest.dcv;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ContextConfiguration {

    private List<String> exclusions = new ArrayList<>();

    public void excludes(String className) {
        this.exclusions.add(className);
    }

    public List<String> getExclusions() {
        return Collections.unmodifiableList(this.exclusions);
    }
}
