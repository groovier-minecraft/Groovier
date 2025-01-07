package com.ericlam.mc.groovier.bungee

import com.ericlam.mc.eld.AddonInstallation
import com.ericlam.mc.eld.BungeeManageProvider
import com.ericlam.mc.eld.ELDBungee
import com.ericlam.mc.eld.ELDBungeePlugin
import com.ericlam.mc.eld.ServiceCollection
import com.ericlam.mc.groovier.ELDGroovierCore
import com.ericlam.mc.groovier.GroovierCore
import com.ericlam.mc.groovier.ScriptPlugin
import com.ericlam.mc.groovier.scriptloaders.CommandRegister
import com.ericlam.mc.groovier.scriptloaders.EventRegister
import net.md_5.bungee.api.plugin.Plugin
import net.md_5.bungee.config.ConfigurationProvider
import net.md_5.bungee.config.YamlConfiguration

import java.nio.file.Files

@ELDBungee(lifeCycle = BungeeLifeCycle)
class GroovierPlugin extends ELDBungeePlugin implements ScriptPlugin{

    private final GroovierCore core = new ELDGroovierCore()

    @Override
    protected void manageProvider(BungeeManageProvider bungeeManageProvider) {

    }

    @Override
    void bindServices(ServiceCollection serviceCollection) {
        var installation = serviceCollection.getInstallation(AddonInstallation)
        core.bindInstance(Plugin, this)
        core.bindRegisters(CommandRegister, new BungeeCommandRegister(this))
        core.bindRegisters(EventRegister, new BungeeEventRegister(this))
        core.onLoad(this, installation)
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
