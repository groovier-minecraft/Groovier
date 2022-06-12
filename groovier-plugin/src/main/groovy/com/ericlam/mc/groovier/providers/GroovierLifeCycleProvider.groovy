package com.ericlam.mc.groovier.providers


import com.ericlam.mc.groovier.ScriptLoader
import com.ericlam.mc.groovier.scriptloaders.GroovierLifeCycle

import javax.inject.Inject
import javax.inject.Provider

class GroovierLifeCycleProvider implements Provider<GroovierLifeCycle> {

    private final GroovierLifeCycle lifeCycle

    @Inject
    GroovierLifeCycleProvider(Set<ScriptLoader> loaders) {
        this.lifeCycle = loaders.find { loader -> loader instanceof GroovierLifeCycle } as GroovierLifeCycle
        if (this.lifeCycle == null) throw new IllegalStateException("No life cycle found.")
    }

    @Override
    GroovierLifeCycle get() {
        return lifeCycle
    }
}
