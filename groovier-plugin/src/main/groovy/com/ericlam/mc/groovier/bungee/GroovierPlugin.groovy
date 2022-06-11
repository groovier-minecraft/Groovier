package com.ericlam.mc.groovier.bungee

import com.ericlam.mc.groovier.GroovierCore
import com.ericlam.mc.groovier.ScriptPlugin
import com.ericlam.mc.groovier.relodables.CommandRegister
import com.ericlam.mc.groovier.relodables.EventRegister
import net.md_5.bungee.api.plugin.Plugin
import net.md_5.bungee.config.ConfigurationProvider
import net.md_5.bungee.config.YamlConfiguration

import java.nio.file.Files

class GroovierPlugin extends Plugin implements ScriptPlugin {

    private final GroovierCore core = new GroovierCore()

    @Override
    void onLoad() {
        core.bindInstance(Plugin.class, this)
        core.bindRegisters(CommandRegister.class, new BungeeCommandRegister(this))
        core.bindRegisters(EventRegister.class, new BungeeEventRegister(this))
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
        if (!getPluginFolder().exists()) getPluginFolder().mkdirs()
        YamlConfiguration yamlConfiguration = ConfigurationProvider.getProvider(YamlConfiguration.class) as YamlConfiguration
        File configFile = new File(getPluginFolder(), "config.yml")
        if (!configFile.exists()) {
            var stream = super.getResourceAsStream("config.yml")
            Files.copy(stream, configFile.toPath())
        }
        var config = yamlConfiguration.load(configFile)
        var copyDefault = config.getBoolean("CopyDefaults")
        if (!copyDefault) return
        try {
            core.copyFromJar("bungee", getPluginFolder().toPath())
            core.copyFromJar("services", getPluginFolder().toPath())
        } catch (URISyntaxException | IOException e) {
            getLogger().warning("Failed to copy resources: " + e.getMessage())
            e.printStackTrace()
        }
    }
}
