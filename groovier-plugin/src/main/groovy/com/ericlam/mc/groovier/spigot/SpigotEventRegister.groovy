package com.ericlam.mc.groovier.spigot

import com.ericlam.mc.groovier.GroovierAPI
import com.ericlam.mc.groovier.GroovierCore
import com.ericlam.mc.groovier.ValidateFailedException
import com.ericlam.mc.groovier.relodables.EventRegister
import net.bytebuddy.ByteBuddy
import net.bytebuddy.implementation.MethodDelegation
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin


class SpigotEventRegister implements EventRegister {

    private final JavaPlugin plugin
    private final GroovierAPI api


    private final Set<Listener> registered = new HashSet<>();

    SpigotEventRegister(JavaPlugin plugin) {
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

                Bukkit.getPluginManager().registerEvents(listener, plugin)
                this.registered.add(listener)


            } catch (Exception e) {
                e.printStackTrace()
                plugin.logger.warning("error while registering event script: ${scriptCls.simpleName}")
            }
        }
    }

    @Override
    void unregister(Set<Class<?>> eventScripts) {
        this.registered.forEach(HandlerList::unregisterAll)
        this.registered.clear()
    }
}
