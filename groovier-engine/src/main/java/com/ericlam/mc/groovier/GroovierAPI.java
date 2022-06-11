package com.ericlam.mc.groovier;

import com.google.inject.Injector;

import javax.inject.Provider;

public interface GroovierAPI {


    void addReloadable(Class<? extends ScriptLoader> reloadable);

    <T extends ScriptValidator> void bindRegisters(Class<T> validator, T ins);

    <T> void bindInstance(Class<T> type, T ins);

    <T, V extends T> void bindType(Class<T> type, Class<V> clazz);

    <T, P extends Provider<T>> void bindProvider(Class<T> type, Class<P> clazz);

    Injector getBaseInjector();

    ServiceInjector getServiceInjector();

    ArgumentParser getArgumentParser();

}
