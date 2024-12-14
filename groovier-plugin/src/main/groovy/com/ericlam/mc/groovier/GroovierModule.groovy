package com.ericlam.mc.groovier

import com.google.inject.AbstractModule
import com.google.inject.Module
import com.google.inject.Scopes
import com.google.inject.multibindings.Multibinder

import javax.inject.Provider

@SuppressWarnings(["rawtypes", "unchecked"])
class GroovierModule extends AbstractModule {

    private final Set<Class<? extends ScriptLoader>> reloadableSet = new HashSet<>()

    private final Map<Class, Object> registerMap = new HashMap<>()
    private final Map<Class, Class> classMap = new HashMap<>()
    private final Map<Class, Class<Provider<?>>> providerMap = new HashMap<>()

    private final Set<Module> modules = new HashSet<>()

    private final GroovyClassLoader classLoader = new GroovyClassLoader(getClass().getClassLoader())

    private ScriptPlugin scriptPlugin

    @Override
    protected void configure() {
        if (scriptPlugin == null) {
            throw new IllegalStateException("scriptPluginClass is not set")
        }
        bind(GroovyClassLoader.class).toInstance(classLoader)
        bind(ScriptCacheManager.class).to(GroovierCacheManager.class).in(Scopes.SINGLETON)
        bind(ScriptPlugin.class).toInstance(scriptPlugin)
        reloadableSet.forEach(reloadable -> {
            Multibinder<ScriptLoader> reloadableBinder = Multibinder.newSetBinder(binder(), ScriptLoader.class)
            reloadableBinder.addBinding().to(reloadable).in(Scopes.SINGLETON)
        })
        registerMap.forEach((type, obj) -> bind(type).toInstance(obj))
        classMap.forEach((type, clazz) -> bind(type).to(clazz).in(Scopes.SINGLETON))
        providerMap.forEach((type, provider) -> bind(type).toProvider(provider).in(Scopes.SINGLETON))
        modules.forEach(this::install)
    }


    void addReloadable(Class<? extends ScriptLoader> reloadable) {
        reloadableSet.add(reloadable)
    }

    void bindScriptPlugin(ScriptPlugin plugin) {
        this.scriptPlugin = plugin
    }

    def <T> void bindInstance(Class<T> type, T ins) {
        registerMap.put(type, ins)
    }

    def <T, V extends T> void bindType(Class<T> type, Class<V> clazz) {
        classMap.put(type, clazz)
    }

    def <T, P extends Provider<T>> void bindProvider(Class<T> type, Class<P> clazz) {
        providerMap.put(type, clazz)
    }

    def <T extends ScriptValidator> void bindRegisters(Class<T> validator, T ins) {
        this.registerMap.put(validator, ins)
    }

    def installModule(Module module) {
        this.modules.add(module)
    }

}
