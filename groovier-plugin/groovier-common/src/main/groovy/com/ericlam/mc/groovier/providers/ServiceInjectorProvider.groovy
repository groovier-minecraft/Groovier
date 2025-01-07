package com.ericlam.mc.groovier.providers

import com.ericlam.mc.groovier.ScriptLoader
import com.ericlam.mc.groovier.ServiceInjector

import javax.inject.Inject
import javax.inject.Provider

class ServiceInjectorProvider implements Provider<ServiceInjector> {


    private final ServiceInjector serviceInjector

    @Inject
    ServiceInjectorProvider(Set<ScriptLoader> loaders) {
        this.serviceInjector = loaders.find { loader -> loader instanceof ServiceInjector } as ServiceInjector
        if (this.serviceInjector == null) throw new IllegalStateException("No service injector found.")
    }

    @Override
    ServiceInjector get() {
        return serviceInjector
    }
}
