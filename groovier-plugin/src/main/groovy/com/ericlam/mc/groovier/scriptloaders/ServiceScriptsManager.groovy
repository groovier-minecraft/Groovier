package com.ericlam.mc.groovier.scriptloaders

import com.ericlam.mc.groovier.ScriptCacheManager
import com.ericlam.mc.groovier.ScriptLoader
import com.ericlam.mc.groovier.ScriptPlugin
import com.ericlam.mc.groovier.ServiceInjector
import com.ericlam.mc.groovier.ValidateFailedException
import com.google.inject.Injector

import javax.inject.Inject

class ServiceScriptsManager implements ScriptLoader, ServiceInjector {
    private final Set<Class<?>> serviceScripts = new HashSet<>()

    @Inject
    private Injector base
    @Inject
    private ScriptPlugin plugin
    @Inject
    private ScriptCacheManager cacheManager

    private Injector injector

    @Override
    void unload() {
        injector = null
        serviceScripts.clear()
    }

    @Override
    void load(GroovyClassLoader classLoader) {
        File servicesFolder = new File(plugin.getPluginFolder(), "services")
        var files = servicesFolder.listFiles()

        if (files == null) {
            plugin.getLogger().info("No services scripts found.")
            return
        }

        for (File file : files) {
            if (file.isFile() && file.getName().endsWith(".groovy")) {
                try {
                    var script = cacheManager.getScriptOrLoad(file, classLoader)
                    serviceScripts.add(script)
                } catch (ValidateFailedException e) {
                    plugin.getLogger().warning("Service Script '${file.getName()}' validation failed: ${e.getMessage()}")
                } catch (Exception e) {
                    e.printStackTrace()
                    plugin.getLogger().warning("Failed to load service script: " + file.getName())
                }
            }
        }

        this.injector = base.createChildInjector(new ServiceModule(serviceScripts))
        plugin.getLogger().info("service injector is ready.")
    }

    @Override
    Optional<Injector> get() {
        return Optional.ofNullable(this.injector)
    }

    @Override
    int getPriority() {
        return 0
    }
}
