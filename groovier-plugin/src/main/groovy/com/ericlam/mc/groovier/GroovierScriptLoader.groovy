package com.ericlam.mc.groovier

import com.ericlam.mc.groovier.scriptloaders.GroovierLifeCycle

import javax.inject.Inject
import java.util.concurrent.CompletableFuture

class GroovierScriptLoader {

    private final List<ScriptLoader> loaders

    @Inject
    GroovierScriptLoader(Set<ScriptLoader> loaders) {
        this.loaders = loaders.sort().toList()
    }


    @Inject
    private ScriptPlugin plugin
    @Inject
    private GroovierLifeCycle lifeCycle
    @Inject
    private ScriptCacheManager cacheManager
    @Inject
    private GroovyClassLoader classLoader

    CompletableFuture<Void> loadAllScripts() {
        CompletableFuture<Void> future = new CompletableFuture<>()
        CompletableFuture.runAsync(() -> {
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
            loaders.forEach(loader -> {
                plugin.getLogger().info("Initializing ${loader.class.simpleName}")
                loader.afterLoad()
                plugin.getLogger().info("${loader.class.simpleName} initializing completed.")
            })
            ((GroovierCacheManager)cacheManager).flush()
        }).whenComplete((v, ex ) -> {
            if (ex != null){
                future.completeExceptionally(ex)
                return
            }
            plugin.logger.info("All Scripts loaded.")
            plugin.runSyncTask(() -> {
                lifeCycle.onScriptLoad()
                future.complete(v)
            })
        })
        return future
    }

    void unloadAllScripts() {
        lifeCycle.onScriptUnload()
        loaders.forEach(loader -> {
            plugin.getLogger().info("Unloading ${loader.class.simpleName}")
            loader.unload()
            plugin.getLogger().info("${loader.class.simpleName} unloaded.")
        })
    }

    CompletableFuture<Void> reloadAllScripts() {
        this.unloadAllScripts()
        return this.loadAllScripts()
    }

}
