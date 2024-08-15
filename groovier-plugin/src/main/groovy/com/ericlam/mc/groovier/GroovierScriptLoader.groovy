package com.ericlam.mc.groovier

import com.ericlam.mc.groovier.scriptloaders.GroovierLifeCycle

import javax.inject.Inject
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicBoolean

class GroovierScriptLoader {

    private final AtomicBoolean loading = new AtomicBoolean(false)
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
        this.loading.compareAndSet(false, true)
        CompletableFuture<Void> future = new CompletableFuture<>()
        plugin.runAsyncTask {
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
            plugin.logger.info("All Scripts loaded.")
            plugin.runSyncTask {
                lifeCycle.onScriptLoad()
                this.loading.compareAndSet(true, false)
                future.complete(null)
            }
        }
        return future
    }

    void unloadAllScripts() {
        this.loading.compareAndSet(false, true)
        lifeCycle.onScriptUnload()
        loaders.forEach(loader -> {
            plugin.getLogger().info("Unloading ${loader.class.simpleName}")
            loader.unload()
            plugin.getLogger().info("${loader.class.simpleName} unloaded.")
        })
        this.loading.compareAndSet(true, false)
    }

    CompletableFuture<Void> reloadAllScripts() {
        if (loading.get()) {
            return CompletableFuture.failedFuture(new ScriptLoadingException())
        }
        this.unloadAllScripts()
        return this.loadAllScripts()
    }

}
