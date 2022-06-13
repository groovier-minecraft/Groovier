package com.ericlam.mc.groovier.spigot

import com.ericlam.mc.groovier.GroovierCore
import com.ericlam.mc.groovier.ScriptPlugin
import com.ericlam.mc.groovier.scriptloaders.CommandRegister
import com.ericlam.mc.groovier.scriptloaders.EventRegister
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.annotations.NotNull

class GroovierPlugin extends JavaPlugin implements ScriptPlugin {

    private final GroovierCore core = new GroovierCore()

    @Override
    void onLoad() {
        core.bindInstance(JavaPlugin.class, this)
        core.bindRegisters(CommandRegister.class, new SpigotCommandRegister(this))
        core.bindRegisters(EventRegister.class, new SpigotEventRegister(this))
        core.onLoad(this)
    }

    @Override
    void onEnable() {
        core.onEnable(this)
    }

    @Override
    void onDisable() {
        core.onDisable(this)
    }

    @Override
    File getPluginFolder() {
        return super.getDataFolder()
    }

    @Override
    void copyResources() {
        saveDefaultConfig()
        var copyDefault = config.getBoolean("CopyDefaults")
        if (!copyDefault) return
        try {
            core.copyFromJar("spigot", getPluginFolder().toPath())
            core.copyFromJar("common", getPluginFolder().toPath())
        } catch (URISyntaxException | IOException e) {
            getLogger().warning("Failed to copy resources: " + e.getMessage())
            e.printStackTrace()
        }
    }

    @Override
    void runSyncTask(Runnable runnable) {
        getServer().getScheduler().runTask(this, runnable)
    }

    @Override
    boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!command.getName().equalsIgnoreCase("groovier")) return false
        if (!sender.hasPermission("groovier.use")) {
            sender.sendMessage("${ChatColor.RED}no permission")
            return
        }
        if (args.length == 0) {
            sender.sendMessage("Usage: /groovier reload | version")
            return true
        }
        var cmd = args[0].toLowerCase()
        switch (cmd){
            case "reload":
                core.reloadAllScripts().whenComplete{v, ex ->
                    if (ex != null){
                        ex.printStackTrace()
                        sender.sendMessage("${ChatColor.RED}Failed to reload scripts: " + ex.getMessage())
                    } else {
                        sender.sendMessage("${ChatColor.GREEN}Successfully reloaded scripts")
                    }
                }
                return true
            case "version":
                sender.sendMessage("Groovier v${getDescription().getVersion()} by ${getDescription().getAuthors().join(", ")}")
                return true
            default:
                sender.sendMessage("Usage: /groovier reload | version")
                return true
        }
    }
}
