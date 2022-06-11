package com.ericlam.mc.groovier;

import groovy.lang.GroovyClassLoader;

/**
 * a script loader interface
 */
public interface ScriptLoader extends Comparable<ScriptLoader> {

    /**
     * do when unloading scripts
     */
    void unload();

    /**
     * do when loading scripts
     * @param classLoader groovy class loader
     */
    void load(GroovyClassLoader classLoader);

    /**
     * do after loading scripts
     */
    default void afterLoad() {
    }

    /**
     * priority of script loading order
     * @return priority
     */
    default int getPriority() {
        return 10;
    }

    @Override
    default int compareTo(ScriptLoader o) {
        return Integer.compare(getPriority(), o.getPriority());
    }
}
