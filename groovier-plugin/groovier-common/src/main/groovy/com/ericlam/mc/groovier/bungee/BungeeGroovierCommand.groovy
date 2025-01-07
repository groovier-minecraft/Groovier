package com.ericlam.mc.groovier.bungee

import com.ericlam.mc.groovier.GroovierCore
import com.ericlam.mc.groovier.ScriptLoader
import com.ericlam.mc.groovier.ScriptLoadingException
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.plugin.Command
import net.md_5.bungee.api.plugin.Plugin
import net.md_5.bungee.api.plugin.TabExecutor

class BungeeGroovierCommand extends Command implements TabExecutor {

    private final GroovierCore core
    private final Plugin plugin

    BungeeGroovierCommand(GroovierCore core, Plugin plugin) {
        super("groovier", "groovier.use")
        this.core = core
        this.plugin = plugin
    }

    @Override
    void execute(CommandSender sender, String[] args) {
        if (hasPermission(sender)) {
            sender.sendMessage(TextComponent.fromLegacy("${ChatColor.RED}no permission."))
        }
        if (args.length == 0) {
            sender.sendMessage(TextComponent.fromLegacy("Usage: /groovier reload [script] | version"))
            return
        }
        if (args[0].equalsIgnoreCase("reload")) {
            if (args.length == 1) {
                core.reloadAllScripts().whenComplete((v, ex) -> {
                    if (ex != null) {
                        if (ex instanceof ScriptLoadingException) {
                            sender.sendMessage(TextComponent.fromLegacy("${ChatColor.GOLD}Script is still loading, please wait until complete."))
                        } else {
                            sender.sendMessage(TextComponent.fromLegacy("${ChatColor.RED}Failed to reload scripts: " + ex.getMessage()))
                            ex.printStackTrace()
                        }
                    } else {
                        sender.sendMessage(TextComponent.fromLegacy("${ChatColor.GREEN}Successfully reloaded scripts"))
                    }
                })
            } else {
                def script = args[1]
                try {
                    def scriptClass = Class.forName(script) as Class<? extends ScriptLoader>
                    core.reloadScript(scriptClass).whenComplete((v, ex) -> {
                        if (ex != null) {
                            if (ex instanceof ScriptLoadingException) {
                                sender.sendMessage(TextComponent.fromLegacy("${ChatColor.GOLD}script ${scriptClass.simpleName} is still loading, please wait until complete."))
                            } else {
                                sender.sendMessage(TextComponent.fromLegacy("${ChatColor.RED}Failed to reload script ${scriptClass.simpleName}: " + ex.getMessage()))
                                ex.printStackTrace()
                            }
                        } else {
                            sender.sendMessage(TextComponent.fromLegacy("${ChatColor.GREEN}Successfully reloaded script ${scriptClass.simpleName}"))
                        }
                    })
                } catch (ClassNotFoundException ignored) {
                    sender.sendMessage(TextComponent.fromLegacy("${ChatColor.RED}Script class $script not found."))
                }
            }
            return
        } else if (args[0].equalsIgnoreCase("version")) {
            sender.sendMessage(TextComponent.fromLegacy("Groovier v${plugin.getDescription().getVersion()} by ${plugin.getDescription().getAuthor()}"))
            return
        }
        sender.sendMessage(TextComponent.fromLegacy("Usage: /groovier reload [script] | version"))
    }

    @Override
    Iterable<String> onTabComplete(CommandSender sender, String[] args) {
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
