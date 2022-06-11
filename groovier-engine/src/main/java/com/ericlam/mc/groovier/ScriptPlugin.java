package com.ericlam.mc.groovier;

import java.io.File;
import java.util.logging.Logger;

public interface ScriptPlugin {

    File getPluginFolder();

    void copyResources();

    Logger getLogger();

}
