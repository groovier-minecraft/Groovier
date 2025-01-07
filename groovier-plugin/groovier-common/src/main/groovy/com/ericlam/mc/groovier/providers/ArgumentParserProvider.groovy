package com.ericlam.mc.groovier.providers

import com.ericlam.mc.groovier.ArgumentParser
import com.ericlam.mc.groovier.ScriptLoader

import javax.inject.Inject
import javax.inject.Provider

class ArgumentParserProvider implements Provider<ArgumentParser> {

    private final ArgumentParser argumentParser

    @Inject
    ArgumentParserProvider(Set<ScriptLoader> loaders){
        this.argumentParser = loaders.find { loader -> loader instanceof ArgumentParser } as ArgumentParser
        if (this.argumentParser == null) throw new IllegalStateException("No argument parser found.")
    }

    @Override
    ArgumentParser get() {
        return argumentParser
    }
}
