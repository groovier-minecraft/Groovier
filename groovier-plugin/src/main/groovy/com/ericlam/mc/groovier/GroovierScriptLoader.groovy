package com.ericlam.mc.groovier

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


    void loadAllScripts() {
        loaders.forEach(loader -> {
            plugin.getLogger().info("Loading ${loader.class.simpleName}")
            loader.load(classLoader)
            plugin.getLogger().info("${loader.class.simpleName} loading completed.")
        })
        loaders.forEach(loader -> loader.afterLoad())
    }

    void unloadAllScripts() {
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
