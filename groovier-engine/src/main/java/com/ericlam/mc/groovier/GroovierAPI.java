package com.ericlam.mc.groovier;

import com.google.inject.Injector;

import javax.inject.Provider;

/**
 * api of groovier
 */
public interface GroovierAPI {

    /**
     * add new scripts type loader
     * @param scriptLoader script type loader
     */
    void addScriptLoader(Class<? extends ScriptLoader> scriptLoader);

    /**
     * add new registrable script type for plugin
     * @param validator registrable script type
     * @param ins       instance
     * @param <T>      registrable script type
     */
    <T extends ScriptValidator> void bindRegisters(Class<T> validator, T ins);

    /**
     * bindInstance for guice
     * @param type    class type
     * @param ins    instance
     * @param <T>   class type
     */
    <T> void bindInstance(Class<T> type, T ins);

    /**
     * bind class type for guice
     * @param type class type
     * @param clazz class
     * @param <T> class type
     * @param <V> class
     */
    <T, V extends T> void bindType(Class<T> type, Class<V> clazz);

    /**
     * bind provider for guice
     * @param type class type
     * @param clazz class
     * @param <T> class type
     * @param <P> provider type
     */
    <T, P extends Provider<T>> void bindProvider(Class<T> type, Class<P> clazz);

    /**
     * get injector (without service injection) for guice
     * @return injector
     */
    Injector getBaseInjector();

    /**
     * get injector (with service injection) for guice
     * @return injector
     */
    ServiceInjector getServiceInjector();

    /**
     * get argument parser
     * @return argument parser
     */
    ArgumentParser getArgumentParser();

}
