package com.ericlam.mc.groovier;

import java.io.File;
import java.util.logging.Logger;

/**
 * for multi platform (bungee and spigot)
 */
public interface ScriptPlugin {

    File getPluginFolder();

    void copyResources();

    Logger getLogger();


    void runSyncTask(Runnable runnable);

}
