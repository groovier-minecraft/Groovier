package com.ericlam.mc.groovier.bungee

import com.ericlam.mc.groovier.GroovierAPI
import com.ericlam.mc.groovier.GroovierCore
import com.ericlam.mc.groovier.ValidateFailedException
import com.ericlam.mc.groovier.scriptloaders.EventRegister
import net.bytebuddy.ByteBuddy
import net.bytebuddy.implementation.MethodDelegation
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.api.plugin.Plugin
import net.md_5.bungee.event.EventHandler

class BungeeEventRegister implements EventRegister {

    private final Set<Listener> registered = new HashSet<>()

    private final Plugin plugin
    private final GroovierAPI api

    BungeeEventRegister(Plugin plugin) {
        this.plugin = plugin
        this.api = GroovierCore.api
    }

    @Override
    void validate(Class<?> scriptClass) throws ValidateFailedException {
        var anyHandler = Arrays.stream(scriptClass.getMethods()).anyMatch(m -> m.isAnnotationPresent(EventHandler.class))

        if (!anyHandler) {
            throw new ValidateFailedException("script class must have at least one method annotated with @EventHandler")
        }
    }

    @Override
    void register(Set<Class<?>> eventScripts) {
        var opt = api.serviceInjector.get()
        if (opt.isEmpty()) {
            throw new IllegalStateException("Failed to get injector provider, have you putting the ServiceScriptManager order to the first?")
        }
        var injector = opt.get()
        for (Class<?> scriptCls in eventScripts) {

            try {
                var eventHandlers = scriptCls.methods.findAll { m -> m.isAnnotationPresent(EventHandler.class) }

                Object delegate = injector.getInstance(scriptCls as Class<Object>)

                var listenerReshape = new ByteBuddy().subclass(Listener.class).name("${scriptCls.simpleName}_Reshaped")

                for (final def method in eventHandlers) {
                    var eventHandler = method.getAnnotation(EventHandler.class)
                    listenerReshape = listenerReshape.define(method).intercept(MethodDelegation.to(delegate)).annotateMethod(eventHandler)
                }

                var listenerCls = listenerReshape.make().load(injector.getInstance(GroovyClassLoader.class)).loaded

                var listener = listenerCls.getDeclaredConstructor().newInstance()

                ProxyServer.instance.pluginManager.registerListener(plugin, listener)
                this.registered.add(listener)


            } catch (Exception e) {
                e.printStackTrace()
                plugin.logger.warning("error while registering event script: ${scriptCls.simpleName}")
            }
        }
    }

    @Override
    void unregister(Set<Class<?>> eventScripts) {
        this.registered.forEach(listener -> ProxyServer.instance.pluginManager.unregisterListener(listener))
        this.registered.clear()
    }
}
