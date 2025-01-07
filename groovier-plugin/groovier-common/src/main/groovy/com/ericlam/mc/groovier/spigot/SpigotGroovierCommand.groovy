package com.ericlam.mc.groovier.spigot

import com.ericlam.mc.groovier.GroovierCore
import com.ericlam.mc.groovier.ScriptLoader
import com.ericlam.mc.groovier.ScriptLoadingException
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.annotations.NotNull

class SpigotGroovierCommand implements CommandExecutor, TabCompleter {

    private final JavaPlugin plugin
    private final GroovierCore core

    SpigotGroovierCommand(JavaPlugin plugin, GroovierCore core) {
        this.plugin = plugin
        this.core = core
    }

    @Override
    boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("groovier.use")) {
            sender.sendMessage("${ChatColor.RED}no permission")
            return false
        }
        if (args.length == 0) {
            sender.sendMessage("Usage: /groovier reload [script] | version")
            return true
        }
        var cmd = args[0].toLowerCase()
        switch (cmd) {
            case "reload":
                if (args.length == 1) {
                    core.reloadAllScripts().whenComplete { v, ex ->
                        if (ex != null) {
                            if (ex instanceof ScriptLoadingException) {
                                sender.sendMessage("${ChatColor.GOLD}Script is still loading, please wait until complete.")
                            } else {
                                sender.sendMessage("${ChatColor.RED}Failed to reload scripts: " + ex.getMessage())
                                ex.printStackTrace()
                            }
                        } else {
                            sender.sendMessage("${ChatColor.GREEN}Successfully reloaded scripts")
                        }
                    }
                } else {
                    def script = args[1]
                    try {
                        def scriptClass = Class.forName(script) as Class<? extends ScriptLoader>
                        core.reloadScript(scriptClass).whenComplete((v, ex) -> {
                            if (ex != null) {
                                if (ex instanceof ScriptLoadingException) {
                                    sender.sendMessage("${ChatColor.GOLD}script ${scriptClass.simpleName} is still loading, please wait until complete.")
                                } else {
                                    sender.sendMessage("${ChatColor.RED}Failed to reload script ${scriptClass.simpleName}: " + ex.getMessage())
                                    ex.printStackTrace()
                                }
                            } else {
                                sender.sendMessage("${ChatColor.GREEN}Successfully reloaded script ${scriptClass.simpleName}")
                            }
                        })
                    }catch (ClassNotFoundException ignored) {
                        sender.sendMessage("${ChatColor.RED}Script class $script not found.")
                    }
                }
                return true
            case "version":
                sender.sendMessage("Groovier v${plugin.getDescription().getVersion()} by ${plugin.getDescription().getAuthors().join(", ")}")
                return true
            default:
                sender.sendMessage("Usage: /groovier reload [script] | version")
                return true
        }
    }

    @Override
    List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return ["reload", "version"]
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("reload")) {
            return core.scriptLoaders
                    .stream()
                    .map { it.class.name }
                    .toList()
        }
        return null
    }
}
