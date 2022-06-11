package com.ericlam.mc.groovier.bungee

import com.ericlam.mc.groovier.*
import com.google.inject.Injector
import com.google.inject.TypeLiteral
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.plugin.Plugin

import javax.annotation.Nullable
import java.lang.reflect.Parameter
import java.util.concurrent.ConcurrentHashMap

class BungeeCommandInvoker {

    private final Map<Class<?>, Object> scriptInstanceCache = new ConcurrentHashMap<>()

    private final Map<String, Object> commandScripts
    private final Plugin plugin

    private final ServiceInjector serviceInjector
    private final ArgumentParser argumentParser

    BungeeCommandInvoker(Map<String, Object> commandScripts, Plugin plugin) {
        this.commandScripts = commandScripts
        this.plugin = plugin
        this.serviceInjector = GroovierCore.api.serviceInjector
        this.argumentParser = GroovierCore.api.argumentParser
        var injector = serviceInjector.get().orElseThrow(() -> new IllegalStateException("Service injector is not initialized."))
        this.initializeScripts(this.commandScripts, injector)
    }

    private void initializeScripts(Map<String, Object> scripts, Injector injector) {
        scripts.forEach((name, o) -> {
            if (o instanceof Map<?, ?>) {
                initializeScripts(o as Map<String, Object>, injector)
            } else if (o instanceof Class<?>) {
                var scriptClass = o as Class<Object>
                var scriptInstance = injector.getInstance(scriptClass)
                scriptInstanceCache.put(scriptClass, scriptInstance)
            }
        })
    }

    @Nullable
    private Object initializeInstance(Class<?> cls, Optional<Injector> injector) {
        if (scriptInstanceCache.containsKey(cls)) return scriptInstanceCache.get(cls)
        if (injector.isEmpty()) return null
        var instance = injector.get().getInstance(cls as Class<Object>)
        scriptInstanceCache.put(cls, instance)
        return instance
    }


    void invokeCommand(CommandSender sender, String command, String[] args) {
        var cmd = command.toLowerCase()
        this.invokeMap(commandScripts, sender, cmd, args)
    }


    private void invokeMap(Map<String, Object> map, CommandSender sender, String cmd, String[] args) {

        if (!map.containsKey(cmd)) {
            sender.sendMessage(text("${ChatColor.RED}unknown command, available commands: ${map.keySet()}"))
            return
        }

        var o = map.get(cmd)

        if (o instanceof Class<?>) {
            var script = o as Class<?>
            this.invokeScript(script, sender, args)
        } else if (o instanceof Map<?, ?>) {

            Map<String, Object> tree = (Map<String, Object>) o

            if (args.length == 0) {
                sender.sendMessage(text("${ChatColor.RED} unknown command, available commands: ${tree.keySet()}"))
                return
            }

            String nextCmd = args[0].toLowerCase()
            String[] nextArgs = Arrays.copyOfRange(args, 1, args.length)
            this.invokeMap(tree, sender, nextCmd, nextArgs)

        } else {
            sender.sendMessage(text("${ChatColor.RED}this command is not supported"))
            plugin.getLogger().warning("Unknown Type for Command: " + cmd + ", Command script must be a class or a map.")
        }
    }

    private void invokeScript(Class<?> cmd, CommandSender sender, String[] args) {
        var injectorOpt = this.serviceInjector.get()

        var script = initializeInstance(cmd, injectorOpt)

        if (script == null) {
            sender.sendMessage(text("${ChatColor.RED}injector is not ready, cannot execute command"))
            return
        }

        var executable = Arrays.stream(cmd.getMethods())
                .filter(m -> m.isAnnotationPresent(CommandScript.class) && m.getParameterCount() > 0)
                .findAny()

        if (executable.isEmpty()) {
            throw new IllegalStateException("cannot find command runner inside script: " + cmd.getName())
        }

        var method = executable.get()

        var permission = method.getAnnotation(CommandScript.class).permission()

        if (!permission.isBlank() && !sender.hasPermission(permission)) {
            sender.sendMessage(text("${ChatColor.RED}you don't have permission to execute this command"))
            return
        }

        if (getRequiredArgumentCount(method.parameters) > args.length) {
            sender.sendMessage(text("${ChatColor.RED} argument not enough: ${getArgumentNames(method.getParameters())}"))
            return
        }

        var params = method.getParameters()
        var parameterArg = new Object[params.length]

        // first must be sender
        parameterArg[0] = sender

        for (int i = 1; i < params.length; i++) {
            var param = params[i]
            if (!param.isAnnotationPresent(CommandArg.class)) {
                throw new IllegalStateException("parameter must be annotated with @CommandArg (except CommandSender)")
            }
            var cmdArgName = param.getAnnotation(CommandArg.class).value()
            var t = param.getParameterizedType()

            // for multiple args
            if (t == String[].class) {
                parameterArg[i] = args
                continue
            } else if (t == new TypeLiteral<List<String>>() {}.type) {
                parameterArg[i] = Arrays.asList(args)
                continue
            }

            try {
                parameterArg[i] = this.argumentParser.parse(t, args[i - 1])
            } catch (ArgumentParseException e) {
                sender.sendMessage(text("${ChatColor.RED} cannot parse argument [${cmdArgName}]: ${e.getMessage()}"))
                return
            }
        }

        try {
            // plugin.logger.info("expected: ${method.genericParameterTypes.toArrayString()}")
            // plugin.logger.info("actual: ${parameterArg.collect { arg -> arg.class }.toArray()}")
            method.invoke(script, parameterArg)
        } catch (Exception e) {
            e.printStackTrace()
            sender.sendMessage(text("${ChatColor.RED} Exception while executing command: ${e.getMessage()}"))
            plugin.getLogger().warning("Exception while executing command: " + cmd.getName())
        }
    }

    private static String getArgumentNames(Parameter[] parameters) {
        return Arrays.stream(parameters)
                .filter(p -> p.isAnnotationPresent(CommandArg.class))
                .map(p -> {
                    var arg = p.getAnnotation(CommandArg.class)
                    if (arg.optional()) {
                        return String.format("[%s]", arg.value())
                    } else {
                        return String.format("<%s>", arg.value())
                    }
                })
                .toList().join(", ")
    }

    private static int getRequiredArgumentCount(Parameter[] parameters){
        return parameters
                .findAll { p -> p.isAnnotationPresent(CommandArg.class) }
                .collect { p -> p.getAnnotation(CommandArg.class) }
                .findAll { a -> !a.optional() }
                .size()
    }

    List<String> invokeTabComplete(CommandSender sender, String command, String[] args) {
        return this.invokeTabs(this.commandScripts, sender, command.toLowerCase(), args)
    }

    private List<String> invokeTabs(Map<String, Object> map, CommandSender sender, String cmd, String[] args) {
        if (!map.containsKey(cmd)) return map.keySet().toList()

        var o = map.get(cmd)

        if (o instanceof Class<?>) {

            var scriptCls = o as Class<Object>
            var injectorOptional = this.serviceInjector.get()

            var script = initializeInstance(scriptCls, injectorOptional)

            if (script == null) {
                plugin.getLogger().warning("injector is not ready, cannot invoke tab complete")
                return null
            }

            try {
                return script.invokeMethod("tabComplete", [sender, args]) as List<String>
            } catch (MissingMethodException ignored) {
                //plugin.getLogger().warning(e.getMessage())
                return null
            }

        } else if (o instanceof Map<?, ?>) {

            Map<String, Object> tree = (Map<String, Object>) o

            if (args.length == 0) {
                return tree.keySet().toList()
            }

            String nextCmd = args[0].toLowerCase()
            String[] nextArgs = Arrays.copyOfRange(args, 1, args.length)
            return this.invokeTabs(tree, sender, nextCmd, nextArgs)

        } else {
            return null
        }

    }


    private static BaseComponent[] text(String str) {
        return TextComponent.fromLegacyText(str)
    }

}
