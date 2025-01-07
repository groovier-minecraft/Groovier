package com.ericlam.mc.groovier.scriptloaders

import com.ericlam.mc.groovier.*
import com.ericlam.mc.groovier.lifecycle.OnDisable
import com.ericlam.mc.groovier.lifecycle.OnEnable
import com.ericlam.mc.groovier.lifecycle.OnScriptLoad
import com.ericlam.mc.groovier.lifecycle.OnScriptUnload

import javax.inject.Inject

class LifeCycleScriptsManager implements ScriptLoader, ScriptValidator, GroovierLifeCycle {

    private final Set<Class<?>> lifeCycleScripts = new HashSet<>()

    private final Set<Runnable> onEnableMethods = new HashSet<>()
    private final Set<Runnable> onDisableMethods = new HashSet<>()
    private final Set<Runnable> onLoadMethods = new HashSet<>()
    private final Set<Runnable> onUnloadMethods = new HashSet<>()

    @Inject
    private ScriptPlugin plugin
    @Inject
    private GroovierAPI api
    @Inject
    private ScriptCacheManager cacheManager

    @Override
    void unload() {
        lifeCycleScripts.clear()
        onLoadMethods.clear()
        onUnloadMethods.clear()
        onDisableMethods.clear()
        onEnableMethods.clear()
    }

    @Override
    void load(GroovyClassLoader classLoader) {
        File lifecycleFolder = new File(plugin.getPluginFolder(), "lifecycles")
        var files = lifecycleFolder.listFiles()

        if (files == null) {
            plugin.getLogger().info("No lifecycles scripts found.")
            return
        }

        for (File file : files) {
            if (file.isFile() && file.getName().endsWith(".groovy")) {
                try {
                    var script = cacheManager.getScriptOrLoad(file, classLoader)
                    this.validate(script)

                    lifeCycleScripts.add(script)

                } catch (ValidateFailedException e) {
                    plugin.getLogger().warning("Lifecycle Script '${file.getName()}' validation failed: ${e.getMessage()}")
                } catch (Exception e) {
                    e.printStackTrace()
                    plugin.getLogger().warning("Failed to load lifecycle script: " + file.getName())
                }
            }
        }
    }

    @Override
    void afterLoad() {
        var injectorReady = api.serviceInjector.get()
        if (injectorReady.empty) {
            throw new IllegalStateException("No service injector found.")
        }
        var injector = injectorReady.get()
        for (Class<Object> script : lifeCycleScripts) {

            var instance = injector.getInstance(script)

            script.methods.findAll { m -> m.isAnnotationPresent(OnEnable.class) }
                    .each { m -> onEnableMethods.add { m.invoke(instance) } }

            script.methods.findAll({ m -> m.isAnnotationPresent(OnDisable.class) })
                    .each { m -> onDisableMethods.add { m.invoke(instance) } }


            script.methods.findAll({ m -> m.isAnnotationPresent(OnScriptLoad.class) })
                    .each { m -> onLoadMethods.add { m.invoke(instance) } }

            script.methods.findAll({ m -> m.isAnnotationPresent(OnScriptUnload.class) })
                    .each { m -> onUnloadMethods.add { m.invoke(instance) } }

        }
    }

    @Override
    void validate(Class<?> scriptClass) throws ValidateFailedException {
        var anyHook = scriptClass.methods.findAll { m ->
            m.isAnnotationPresent(OnEnable.class) ||
                    m.isAnnotationPresent(OnDisable.class) ||
                    m.isAnnotationPresent(OnScriptLoad.class) ||
                    m.isAnnotationPresent(OnScriptUnload.class)
        }
        if (anyHook.empty) {
            throw new ValidateFailedException("No any lifecycle method found.")
        }

        for (final def hook in anyHook) {
            if (hook.parameterCount > 0) {
                throw new ValidateFailedException("lifecycle method must not have any parameter.")
            }
        }

    }

    @Override
    void onEnable() {
        onEnableMethods.each { runner -> runner.run() }
    }

    @Override
    void onDisable() {
        onDisableMethods.each { runner -> runner.run() }
    }

    @Override
    void onScriptLoad() {
        onLoadMethods.each { runner -> runner.run() }
    }

    @Override
    void onScriptUnload() {
        onUnloadMethods.each { runner -> runner.run() }
    }
}
