package com.ericlam.mc.groovier

import com.ericlam.mc.groovier.scriptloaders.GroovierLifeCycle
import groovy.grape.Grape

import javax.inject.Inject

class GroovierScriptLoader {

    private final List<ScriptLoader> loaders

    @Inject
    GroovierScriptLoader(Set<ScriptLoader> loaders){
        this.loaders = loaders.sort().toList()
    }

    @Inject
    private GroovyClassLoader classLoader

    @Inject
    private ScriptPlugin plugin

    @Inject
    private GroovierLifeCycle lifeCycle

    void loadAllScripts() {
        var globalLibraries = new File(plugin.getPluginFolder(), "grapesConfig.groovy")
        if (globalLibraries.exists()) {
            plugin.logger.info("loading global libraries...")
            classLoader.parseClass(globalLibraries)
            plugin.logger.info("global libraries loaded.")
        }
        loaders.forEach(loader -> {
            plugin.getLogger().info("Loading ${loader.class.simpleName}")
            loader.load(classLoader)
            plugin.getLogger().info("${loader.class.simpleName} loading completed.")
        })
        loaders.forEach(loader -> loader.afterLoad())
        lifeCycle.onScriptLoad()
    }

    void unloadAllScripts() {
        lifeCycle.onScriptUnload()
        loaders.forEach(loader -> {
            plugin.getLogger().info("Unloading ${loader.class.simpleName}")
            loader.unload()
            plugin.getLogger().info("${loader.class.simpleName} unloaded.")
        })
    }

    void reloadAllScripts() {
        this.unloadAllScripts()
        this.loadAllScripts()
    }

}
