package com.ericlam.mc.groovier.scriptloaders

import com.google.inject.AbstractModule
import com.google.inject.Scopes

class ServiceModule extends AbstractModule {

    private final Set<Class> serviceScripts

    ServiceModule(Set<Class<?>> serviceScripts) {
        this.serviceScripts = serviceScripts
    }

    @Override
    protected void configure() {
        serviceScripts.forEach(service -> bind(service).in(Scopes.SINGLETON))
    }

}
