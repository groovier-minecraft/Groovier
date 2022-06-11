package com.ericlam.mc.groovier.relodables


import com.ericlam.mc.groovier.ScriptLoader
import com.ericlam.mc.groovier.ScriptPlugin
import com.ericlam.mc.groovier.ValidateFailedException

import javax.inject.Inject

class CommandScriptsManager implements ScriptLoader {

    private final Map<String, Object> commandScripts = new HashMap<>()

    @Inject
    private ScriptPlugin plugin
    @Inject
    private CommandRegister commandRegister

    @Override
    void unload() {
        this.commandRegister.unregister(this.commandScripts.keySet())
        this.commandScripts.clear()
    }

    @Override
    void load(GroovyClassLoader classLoader) {
        File commandFolder = new File(plugin.getPluginFolder(), "commands")
        if (!commandFolder.exists()) {
            commandFolder.mkdirs()
        }

        File[] commandFiles = commandFolder.listFiles()

        if (commandFiles == null) {
            plugin.getLogger().info("No command scripts found.")
            return
        }

        this.loadScripFiles(commandFiles, this.commandScripts, classLoader)
    }

    @Override
    void afterLoad() {
        commandRegister.register(this.commandScripts)
    }

    private void loadScripFiles(File[] commandFiles, Map<String, Object> commandTrees, GroovyClassLoader groovyClassLoader) {
        if (commandFiles == null) return
        for (File commandFile : commandFiles) {
            if (commandFile.isDirectory()) {
                var name = commandFile.getName().toLowerCase()
                var nestedFiles = commandFile.listFiles()
                var nestedTrees = new HashMap<String, Object>()
                loadScripFiles(nestedFiles, nestedTrees, groovyClassLoader)
                commandTrees.put(name, nestedTrees)
            } else if (commandFile.isFile()) {
                if (commandFile.getName().endsWith(".groovy")) {
                    try {
                        var name = commandFile.getName().substring(0, commandFile.getName().length() - 7).toLowerCase()
                        var script = (Class<?>) groovyClassLoader.parseClass(commandFile)
                        commandRegister.validate(script)
                        commandTrees.put(name, script)
                    } catch (ValidateFailedException e) {
                        plugin.getLogger().severe("Command Script '${commandFile.getName()}' validation failed: ${e.getMessage()}")
                    } catch (Exception e) {
                        e.printStackTrace()
                        plugin.getLogger().severe("Failed to load command script: " + commandFile.getName())
                    }
                } else {
                    plugin.getLogger().warning("Command script " + commandFile.getName() + " is not a groovy file, skipped")
                }

            }

        }
    }
}
