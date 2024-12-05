package com.ericlam.mc.groovier

import com.ericlam.mc.groovier.scriptloaders.GroovierLifeCycle

import javax.inject.Inject
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicBoolean

class GroovierScriptLoader {

    private final AtomicBoolean reloading = new AtomicBoolean(false)
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

    void addClassPath() {
        classLoader.addClasspath(plugin.pluginFolder.path)
    }

    CompletableFuture<Void> loadAllScripts() {
        CompletableFuture<Void> future = new CompletableFuture<>()
        plugin.runAsyncTask {
            try {
                var globalLibraries = new File(plugin.pluginFolder, "grapesConfig.groovy")
                if (globalLibraries.exists()) {
                    plugin.logger.info("loading global libraries...")
                    classLoader.parseClass(globalLibraries)
                    plugin.logger.info("global libraries loaded.")
                }
                loaders.each { loader ->
                    plugin.logger.info("Loading ${loader.class.simpleName}")
                    loader.load(classLoader)
                    plugin.logger.info("${loader.class.simpleName} loading completed.")
                }
                loaders.each { loader ->
                    plugin.logger.info("Initializing ${loader.class.simpleName}")
                    loader.afterLoad()
                    plugin.logger.info("${loader.class.simpleName} initializing completed.")
                }
                ((GroovierCacheManager)cacheManager).flush()
                plugin.logger.info("All Scripts loaded.")
                plugin.runSyncTask {
                    lifeCycle.onScriptLoad()
                    future.complete(null)
                }
            } catch (Exception e) {
                future.completeExceptionally(e)
            }
        }
        return future
    }

    void unloadAllScripts() {
        lifeCycle.onScriptUnload()
        loaders.each { loader ->
            plugin.logger.info("Unloading ${loader.class.simpleName}")
            loader.unload()
            plugin.logger.info("${loader.class.simpleName} unloaded.")
        }
        classLoader.clearCache()
    }

    CompletableFuture<Void> reloadAllScripts() {
        if (!reloading.compareAndSet(false, true)) {
            return CompletableFuture.failedFuture(new ScriptLoadingException())
        }
        this.unloadAllScripts()
        return this.loadAllScripts().whenComplete { reloading.set(false) }
    }

}
