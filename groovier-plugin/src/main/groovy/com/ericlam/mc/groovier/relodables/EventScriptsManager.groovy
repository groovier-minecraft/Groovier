package com.ericlam.mc.groovier.relodables


import com.ericlam.mc.groovier.ScriptLoader
import com.ericlam.mc.groovier.ScriptPlugin
import com.ericlam.mc.groovier.ValidateFailedException

import javax.inject.Inject

class EventScriptsManager implements ScriptLoader {

    private final Set<Class<?>> eventScripts = new HashSet<>()

    @Inject
    private ScriptPlugin plugin

    @Inject
    private EventRegister eventRegister


    @Override
    void unload() {
        eventRegister.unregister(eventScripts)
        eventScripts.clear()
    }

    @Override
    void load(GroovyClassLoader classLoader) {
        File listenerFolder = new File(plugin.getPluginFolder(), "listeners")
        var files = listenerFolder.listFiles()

        if (files == null) {
            plugin.getLogger().info("No event scripts found.")
            return
        }

        for (File file : files) {
            if (file.isFile() && file.getName().endsWith(".groovy")) {
                try {
                    var script = (Class<?>) classLoader.parseClass(file)
                    eventRegister.validate(script)
                    eventScripts.add(script)
                } catch (ValidateFailedException e) {
                    plugin.getLogger().warning("Event Script '${file.getName()}' validation failed: ${e.getMessage()}")
                } catch (Exception e) {
                    e.printStackTrace()
                    plugin.getLogger().warning("Failed to load event script: " + file.getName())
                }
            }
        }
    }

    @Override
    void afterLoad() {
        this.eventRegister.register(eventScripts)
    }
}
