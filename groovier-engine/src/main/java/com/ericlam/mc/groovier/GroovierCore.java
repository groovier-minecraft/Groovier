package com.ericlam.mc.groovier;

import com.google.inject.Injector;

import javax.inject.Provider;

public class GroovierCore implements GroovierAPI {

    public static GroovierAPI getApi() {
        throw new UnsupportedOperationException("not plugin");
    }

    @Override
    public void addReloadable(Class<? extends ScriptLoader> reloadable) {
        throw new UnsupportedOperationException("not plugin");
    }

    @Override
    public <T extends ScriptValidator> void bindRegisters(Class<T> validator, T ins) {
        throw new UnsupportedOperationException("not plugin");
    }

    @Override
    public <T> void bindInstance(Class<T> type, T ins) {
        throw new UnsupportedOperationException("not plugin");
    }

    @Override
    public <T, V extends T> void bindType(Class<T> type, Class<V> clazz) {
        throw new UnsupportedOperationException("not plugin");
    }

    @Override
    public <T, P extends Provider<T>> void bindProvider(Class<T> type, Class<P> clazz) {

    }


    @Override
    public Injector getBaseInjector() {
        throw new UnsupportedOperationException("not plugin");
    }

    @Override
    public ServiceInjector getServiceInjector() {
        throw new UnsupportedOperationException("not plugin");
    }

    @Override
    public ArgumentParser getArgumentParser() {
        throw new UnsupportedOperationException("not plugin");
    }
}
