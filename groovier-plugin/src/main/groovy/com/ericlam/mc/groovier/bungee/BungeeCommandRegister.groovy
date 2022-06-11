package com.ericlam.mc.groovier.bungee

import com.ericlam.mc.groovier.CommandScript
import com.ericlam.mc.groovier.ValidateFailedException
import com.ericlam.mc.groovier.relodables.CommandRegister
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.plugin.Command
import net.md_5.bungee.api.plugin.Plugin
import net.md_5.bungee.api.plugin.TabExecutor

import javax.annotation.Nullable

class BungeeCommandRegister implements CommandRegister {


    private final Set<Command> registered = new HashSet<>();

    private final Plugin plugin

    BungeeCommandRegister(Plugin plugin) {
        this.plugin = plugin
    }


    @Override
    void validate(Class<?> scriptClass) throws ValidateFailedException {

        var methodOpt = Arrays.stream(scriptClass.getMethods()).filter(m -> m.isAnnotationPresent(CommandScript.class)).findAny()

        if (methodOpt.isEmpty()) {
            throw new ValidateFailedException("Command script must have a method annotated with @Command.")
        }

        var method = methodOpt.get()

        if (method.parameterCount == 0) {
            throw new ValidateFailedException("Command script method must have at least CommandSender parameter.")
        }

        if (method.parameterTypes[0] != CommandSender.class) {
            throw new ValidateFailedException("Command script method must have CommandSender parameter.")
        }

        var tabMethod = scriptClass.getMethods().find { m -> m.getName() == "tabComplete" }
        if (tabMethod == null) return

        if (!Arrays.equals(tabMethod.parameterTypes, [CommandSender.class, String[].class].toArray())) {
            throw new ValidateFailedException("Command script tabComplete method must have CommandSender and String[] parameter.")
        }


    }

    @Override
    void register(Map<String, Object> commandScripts) {
        var invoker = new BungeeCommandInvoker(commandScripts, plugin)
        for (final def cmd in commandScripts.keySet()) {

            var script = commandScripts[cmd]

            CommandScript commandScript = null
            if (script instanceof Class<?>) {
                var cls = script as Class<?>
                commandScript = Optional.ofNullable(cls.getMethods().find { m -> m.isAnnotationPresent(CommandScript.class) })
                        .map { it.getAnnotation(CommandScript.class) }.orElseThrow()
            }

            var bungeeCommand = new BungeeCommand(cmd, commandScript, invoker)
            ProxyServer.instance.pluginManager.registerCommand(plugin, bungeeCommand)
            this.registered.add(bungeeCommand)
        }
    }

    @Override
    void unregister(Set<String> commandScripts) {
        this.registered.forEach(cmd -> ProxyServer.instance.pluginManager.unregisterCommand(cmd))
        this.registered.clear()
    }


    static class BungeeCommand extends Command implements TabExecutor {

        private final BungeeCommandInvoker invoker

        BungeeCommand(String name, @Nullable CommandScript script, BungeeCommandInvoker invoker) {
            super(name, script != null ? script.permission() : null)
            this.invoker = invoker
        }

        @Override
        void execute(CommandSender sender, String[] args) {
            invoker.invokeCommand(sender, name, args)
        }

        @Override
        Iterable<String> onTabComplete(CommandSender sender, String[] args) {
            invoker.invokeTabComplete(sender, name, args)
        }
    }
}
