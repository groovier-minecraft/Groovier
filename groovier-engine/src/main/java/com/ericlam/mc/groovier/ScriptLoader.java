package com.ericlam.mc.groovier;

import groovy.lang.GroovyClassLoader;

public interface ScriptLoader extends Comparable<ScriptLoader> {

    void unload();

    void load(GroovyClassLoader classLoader);

    default void afterLoad() {
    }

    default int getPriority() {
        return 10;
    }

    @Override
    default int compareTo(ScriptLoader o) {
        return Integer.compare(getPriority(), o.getPriority());
    }
}
