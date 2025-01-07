package com.ericlam.mc.groovier.spigot

import com.ericlam.mc.groovier.GroovierCore
import com.ericlam.mc.groovier.MCGroovierCore
import com.ericlam.mc.groovier.ScriptLoadingException
import com.ericlam.mc.groovier.ScriptPlugin
import com.ericlam.mc.groovier.scriptloaders.CommandRegister
import com.ericlam.mc.groovier.scriptloaders.EventRegister
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.annotations.NotNull

class GroovierPlugin extends JavaPlugin implements ScriptPlugin {

    private final GroovierCore core = new MCGroovierCore()

    @Override
    void onLoad() {
        core.bindInstance(JavaPlugin.class, this)
        core.bindRegisters(CommandRegister.class, new SpigotCommandRegister(this))
        core.bindRegisters(EventRegister.class, new SpigotEventRegister(this))
        core.onLoad(this)
    }

    @Override
    void onEnable() {
        core.onEnable()
        getCommand("groovier").setExecutor(new SpigotGroovierCommand(this, core))
    }

    @Override
    void onDisable() {
        core.onDisable()
    }

    @Override
    File getPluginFolder() {
        return super.getDataFolder()
    }

    @Override
    boolean isCopyDefaults() {
        saveDefaultConfig()
        return config.getBoolean("CopyDefaults")
    }

    @Override
    void copyResources() {
        try {
            core.copyFromJar("spigot")
            core.copyFromJar("common")
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
    void runAsyncTask(Runnable runnable) {
        getServer().getScheduler().runTaskAsynchronously(this, runnable)
    }
}
