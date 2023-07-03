package com.ericlam.mc.groovier.bungee

import com.ericlam.mc.groovier.GroovierCore
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

        var groovierCommand = new Command("groovier", "groovier.use") {
            @Override
            void execute(CommandSender sender, String[] args) {
                if (hasPermission(sender)) {
                    sender.sendMessage(TextComponent.fromLegacyText("${ChatColor.RED}no permission."))
                }
                if (args.length == 0) {
                    sender.sendMessage(TextComponent.fromLegacyText("Usage: /groovier reload | version"))
                    return
                }
                if (args[0].equalsIgnoreCase("reload")) {
                    core.reloadAllScripts().whenComplete((v, ex) -> {
                        if (ex != null) {
                            sender.sendMessage(TextComponent.fromLegacyText("${ChatColor.RED}Failed to reload scripts: " + ex.getMessage()))
                            ex.printStackTrace()
                        } else {
                            sender.sendMessage(TextComponent.fromLegacyText("${ChatColor.GREEN}Successfully reloaded scripts"))
                        }
                    })

                    return
                }
                if (args[0].equalsIgnoreCase("version")) {
                    sender.sendMessage(TextComponent.fromLegacyText("Groovier v${getDescription().getVersion()} by ${getDescription().getAuthor()}"))
                    return
                }
                sender.sendMessage(TextComponent.fromLegacyText("Usage: /groovier reload | version"))
            }
        }

        proxy.pluginManager.registerCommand(this, groovierCommand)
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
            core.copyFromJar("common", getPluginFolder().toPath())
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
