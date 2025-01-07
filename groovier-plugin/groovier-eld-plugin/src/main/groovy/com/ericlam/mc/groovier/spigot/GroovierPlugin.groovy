package com.ericlam.mc.groovier.spigot

import com.ericlam.mc.eld.*
import com.ericlam.mc.groovier.ELDGroovierCore
import com.ericlam.mc.groovier.GroovierCore
import com.ericlam.mc.groovier.ScriptPlugin
import com.ericlam.mc.groovier.scriptloaders.CommandRegister
import com.ericlam.mc.groovier.scriptloaders.EventRegister
import org.bukkit.plugin.java.JavaPlugin

@ELDBukkit(lifeCycle = SpigotLifeCycle)
class GroovierPlugin extends ELDBukkitPlugin implements ScriptPlugin {

    private final GroovierCore core = new ELDGroovierCore()

    @Override
    protected void manageProvider(BukkitManagerProvider bukkitManagerProvider) {

    }

    @Override
    void bindServices(ServiceCollection serviceCollection) {
        var installation = serviceCollection.getInstallation(AddonInstallation)
        core.bindInstance(JavaPlugin.class, this)
        core.bindRegisters(CommandRegister, new SpigotCommandRegister(this))
        core.bindRegisters(EventRegister, new SpigotEventRegister(this))
        core.onLoad(this, installation)
    }

    @Override
    File getPluginFolder() {
        return super.dataFolder
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
