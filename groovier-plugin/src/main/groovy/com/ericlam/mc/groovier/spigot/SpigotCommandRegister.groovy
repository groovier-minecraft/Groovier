package com.ericlam.mc.groovier.spigot

import com.ericlam.mc.groovier.CommandScript
import com.ericlam.mc.groovier.ValidateFailedException
import com.ericlam.mc.groovier.relodables.CommandRegister
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.PluginCommand
import org.bukkit.command.SimpleCommandMap
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.SimplePluginManager
import org.bukkit.plugin.java.JavaPlugin

import javax.annotation.Nullable

class SpigotCommandRegister implements CommandRegister {


    private final JavaPlugin plugin

    SpigotCommandRegister(JavaPlugin plugin) {
        this.plugin = plugin
    }

    @Override
    void register(Map<String, Object> commandScripts) {
        var invoker = new SpigotCommandInvoker(commandScripts, plugin)
        this.registerCommands(commandScripts, invoker)
    }


    @Override
    void unregister(Set<String> commandScripts) {
        this.unregisterCommands(commandScripts)
    }

    @Override
    void validate(Class<?> scriptClass) throws ValidateFailedException {

        var methodOpt = Arrays.stream(scriptClass.getMethods()).filter(m -> m.isAnnotationPresent(CommandScript.class)).findAny()

        if (methodOpt.isEmpty()) {
            throw new ValidateFailedException("Command script must have a method annotated with @Command.")
        }

        var method = methodOpt.get()

        if (method.getParameterCount() == 0) {
            throw new ValidateFailedException("Command script method must have at least CommandSender parameter.")
        }

        if (method.getParameterTypes()[0] != CommandSender.class) {
            throw new ValidateFailedException("Command script method must have CommandSender parameter.")
        }


        var tabMethod = scriptClass.getMethods().find { m -> m.getName() == "tabComplete"}
        if (tabMethod == null) return
        //plugin.getLogger().info("expected: ${[CommandSender.class, String[].class].toArray()}")
        //plugin.getLogger().info("actual: ${tabMethod.parameterTypes}")
        if (!Arrays.equals(tabMethod.parameterTypes, [CommandSender.class, String[].class].toArray())) {
            throw new ValidateFailedException("Command script method tabComplete must have CommandSender and String[] parameter.")
        }

    }


    private static SimpleCommandMap getCommandMap() throws Exception {
        var commandMap = Bukkit.server.class.getDeclaredField("commandMap")
        commandMap.setAccessible(true)
        return (SimpleCommandMap) commandMap.get(Bukkit.server)
    }

    private static void syncCommands() throws Exception{
        var syncCommandsMethod = Bukkit.server.class.getMethod("syncCommands")
        syncCommandsMethod.invoke(Bukkit.server)
    }


    @SuppressWarnings("unchecked")
    private void unregisterCommands(Set<String> rootCommands) {
        try {

            SimpleCommandMap map = getCommandMap()
            var knownCommands = SimpleCommandMap.class.getDeclaredField("knownCommands")
            knownCommands.setAccessible(true)
            var commands = (Map<String, Command>) knownCommands.get(map)
            rootCommands.forEach(commands::remove)
            runTaskOrNot { syncCommands()}
        } catch (Exception e) {
            e.printStackTrace()
            plugin.getLogger().warning("failed to unregister commands: " + e.getMessage())
        }
    }

    private void registerCommands(Map<String, Object> commandScripts, SpigotCommandInvoker invoker) {
        try {

            SimpleCommandMap map = getCommandMap()

            for (String rootCommand : commandScripts.keySet()) {
                var o = commandScripts.get(rootCommand)
                String description = null
                if (o instanceof Class<?>) {
                    var cls = (Class<?>) o
                    description = Optional.ofNullable(cls.getMethods().find { m -> m.isAnnotationPresent(CommandScript.class) })
                            .map { it.getAnnotation(CommandScript.class).description() }
                }
                this.registerCommand(rootCommand, invoker, description, map)
            }

            runTaskOrNot { syncCommands() }
        } catch (Exception e) {
            e.printStackTrace()
            plugin.getLogger().warning("failed to get command map: " + e.getMessage())
        }
    }


    private void registerCommand(String cmd, SpigotCommandInvoker invoker, @Nullable String description, SimpleCommandMap map) {
        try {
            var con = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class)
            con.setAccessible(true)
            PluginCommand command = con.newInstance(cmd, plugin)
            command.setExecutor(invoker)
            command.setTabCompleter(invoker)
            command.setDescription(description == null ? "list of commands for " + cmd : description)
            map.register(plugin.getName().toLowerCase(), command)
        } catch (Exception e) {
            e.printStackTrace()
            plugin.getLogger().warning("Failed to register command " + cmd)
        }
    }


    private void runTaskOrNot(Closure<Void> task){
        if(Bukkit.isPrimaryThread()){
            task.call()
        }else{
            Bukkit.getScheduler().runTask(plugin, task)
        }
    }
}
