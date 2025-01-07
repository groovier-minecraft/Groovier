package com.ericlam.mc.groovier.bungee

import com.ericlam.mc.groovier.GroovierCore
import com.ericlam.mc.groovier.MCGroovierCore
import com.ericlam.mc.groovier.ScriptLoader
import com.ericlam.mc.groovier.ScriptLoadingException
import com.ericlam.mc.groovier.ScriptPlugin
import com.ericlam.mc.groovier.scriptloaders.CommandRegister
import com.ericlam.mc.groovier.scriptloaders.EventRegister
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.plugin.Command
import net.md_5.bungee.api.plugin.Plugin
import net.md_5.bungee.config.ConfigurationProvider
import net.md_5.bungee.config.YamlConfiguration

import java.nio.file.Files

class GroovierPlugin extends Plugin implements ScriptPlugin {

    private final GroovierCore core = new MCGroovierCore()

    @Override
    void onLoad() {
        core.bindInstance(Plugin.class, this)
        core.bindRegisters(CommandRegister.class, new BungeeCommandRegister(this))
        core.bindRegisters(EventRegister.class, new BungeeEventRegister(this))
        core.onLoad(this)
    }

    @Override
    void onEnable() {
        core.onEnable()
        proxy.pluginManager.registerCommand(this, new BungeeGroovierCommand(core, this))
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
        if (!getPluginFolder().exists()) getPluginFolder().mkdirs()
        YamlConfiguration yamlConfiguration = ConfigurationProvider.getProvider(YamlConfiguration.class) as YamlConfiguration
        File configFile = new File(getPluginFolder(), "config.yml")
        if (!configFile.exists()) {
            var stream = super.getResourceAsStream("config.yml")
            Files.copy(stream, configFile.toPath())
        }
        var config = yamlConfiguration.load(configFile)
        return config.getBoolean("CopyDefaults")
    }

    @Override
    void copyResources() {
        try {
            core.copyFromJar("bungee")
            core.copyFromJar("common")
        } catch (URISyntaxException | IOException e) {
            getLogger().warning("Failed to copy resources: " + e.getMessage())
            e.printStackTrace()
        }
    }

    @Override
    void runSyncTask(Runnable runnable) {
        runnable.run()
    }

    @Override
    void runAsyncTask(Runnable runnable) {
        proxy.scheduler.runAsync(this, runnable)
    }
}
